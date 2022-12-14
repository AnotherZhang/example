package science.atlarge.opencraft.opencraft.scheduler;

import com.atlarge.yscollector.YSCollector;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import lombok.Getter;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.messaging.Messaging;
import science.atlarge.opencraft.opencraft.util.config.ServerConfig;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Manager for world thread pool.
 *
 * <p>This is a little magical and finnicky, so tread with caution when messing with the phasers
 */
public class WorldScheduler {

    @Getter
    private final Object advanceCondition = new Object();
    private final ExecutorService worldExecutor = Executors.newCachedThreadPool();
    private final Phaser tickBegin = new Phaser(1);
    private final Phaser tickEnd = new Phaser(1);
    private final List<WorldEntry> worlds = new CopyOnWriteArrayList<>();
    private volatile int currentTick = -1;

    /**
     * Returns an immutable list of the currently scheduled worlds.
     *
     * @return the scheduled worlds
     */
    public List<GlowWorld> getWorlds() {
        Builder<GlowWorld> ret = ImmutableList.builder();
        for (WorldEntry entry : worlds) {
            ret.add(entry.world);
        }
        return ret.build();
    }

    /**
     * Returns the world with a given name.
     *
     * @param name the name to look up
     * @return the world with that name, or null if none match
     */
    public GlowWorld getWorld(String name) {
        for (WorldEntry went : worlds) {
            if (went.world.getName().equals(name)) {
                return went.world;
            }
        }
        return null;
    }

    /**
     * Returns the world with a given UUID.
     *
     * @param uid the UUID to look up
     * @return the world with that UUID, or null if none match
     */
    public GlowWorld getWorld(UUID uid) {
        // FIXME: Unnecessary linear time
        for (WorldEntry went : worlds) {
            if (went.world.getUID().equals(uid)) {
                return went.world;
            }
        }
        return null;
    }

    /**
     * Attempts to start scheduled ticks for a world.
     *
     * @param world the world to start ticking
     * @return {@code world} if it is now ticking; null otherwise
     */
    public GlowWorld addWorld(GlowWorld world) {
        WorldEntry went = new WorldEntry(world);
        worlds.add(went);
        try {
            went.task = new WorldThread(world);
            tickBegin.register();
            tickEnd.register();
            worldExecutor.submit(went.task);
            return world;
        } catch (Throwable t) {
            tickBegin.arriveAndDeregister();
            tickEnd.arriveAndDeregister();
            worlds.remove(went);
            return null;
        }
    }

    /**
     * Stops scheduled ticks for a world.
     *
     * @param world the world to stop ticking
     * @return whether the world had been scheduled
     */
    public boolean removeWorld(GlowWorld world) {
        for (WorldEntry entry : worlds) {
            if (entry.world.equals(world)) {
                if (entry.task != null) {
                    entry.task.interrupt();
                }
                worlds.remove(entry);
                return true;
            }
        }
        return false;
    }

    int beginTick() throws InterruptedException {
        tickEnd.awaitAdvanceInterruptibly(currentTick); // Make sure previous tick is complete
        return currentTick = tickBegin.arrive();
    }

    boolean isTickComplete(int tick) {
        return tickEnd.getPhase() > tick || tickEnd.getPhase() < 0;
    }

    void stop() {
        tickBegin.forceTermination();
        tickEnd.forceTermination();
        worldExecutor.shutdown();
        try {
            if (!worldExecutor.awaitTermination(1L, TimeUnit.MINUTES)) {
                worldExecutor.shutdownNow();
            }
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    void doTickEnd() {
        int currentTick = this.currentTick;
        // Mark ourselves as arrived so world threads automatically trigger advance once done
        int endPhase = tickEnd.arriveAndAwaitAdvance();
        if (endPhase != currentTick + 1) {
            GlowServer.logger.warning("Tick end barrier " + endPhase
                    + " has advanced differently from tick begin barrier:" + currentTick + 1);
        }
        synchronized (advanceCondition) {
            advanceCondition.notifyAll();
        }
    }

    /**
     * Flush all outgoing messages to clients.
     */
    public void flushMessages() {
        worlds.stream().map(w -> w.world.getMessagingSystem()).distinct().forEach(Messaging::flush);
    }

    private static class WorldEntry {

        private final GlowWorld world;
        private WorldThread task;

        private WorldEntry(GlowWorld world) {
            this.world = world;
        }
    }

    private class WorldThread extends Thread {

        private final GlowWorld world;

        public WorldThread(GlowWorld world) {
            super("Glowstone-world-" + world.getName());
            this.world = world;
        }

        @Override
        public void run() {
            try {
                while (!isInterrupted() && !tickEnd.isTerminated()) {
                    tickBegin.arriveAndAwaitAdvance();
                    if (ServerConfig.Key.OPENCRAFT_COLLECTOR.equals(true)) {
                        YSCollector.start("world_" + world.getName() + "_tick",
                                "World thread: Duration processing tick.");
                    }
                    try {
                        world.pulse();
                    } catch (Throwable t) {
                        GlowServer.logger.log(Level.SEVERE,
                                "Error occurred while pulsing world " + world.getName(), t);
                        throw t;
                    } finally {
                        tickEnd.arriveAndAwaitAdvance();
                    }
                    if (ServerConfig.Key.OPENCRAFT_COLLECTOR.equals(true)) {
                        YSCollector.stop("world_" + world.getName() + "_tick");
                    }
                }
            } finally {
                tickBegin.arriveAndDeregister();
                tickEnd.arriveAndDeregister();
            }
        }
    }
}
