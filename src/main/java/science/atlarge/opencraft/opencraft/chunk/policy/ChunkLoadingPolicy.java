package science.atlarge.opencraft.opencraft.chunk.policy;

import com.flowpowered.network.Message;
import com.flowpowered.network.session.Session;
import com.google.common.collect.ImmutableMap;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.chunk.AreaOfInterest;
import science.atlarge.opencraft.opencraft.chunk.ChunkManager;
import science.atlarge.opencraft.opencraft.chunk.GlowChunk;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.executor.ChunkRunnable;
import science.atlarge.opencraft.opencraft.executor.PriorityExecutor;
import science.atlarge.opencraft.opencraft.messaging.Messaging;
import science.atlarge.opencraft.opencraft.net.message.play.game.UnloadChunkMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ChunkLoadingPolicy {

    protected final GlowWorld world;

    protected ImmutableMap<GlowPlayer, AreaOfInterest> previousAreas;

    protected final PriorityExecutor<ChunkRunnable> executor;

    protected double qos = 0;
    protected double qos_min = 0;

    public ChunkLoadingPolicy(GlowWorld world) {
        this.world = world;
        previousAreas = ImmutableMap.of();
        executor = new PriorityExecutor<>();
    }

    /**
     * Used by the population policy command to turn text into a policy.
     *
     * @param world the world calling this command
     * @param name  the name of the policy
     * @return a policy object resolved from the name
     */
    public static ChunkLoadingPolicy fromString(GlowWorld world, String name) {
        if (name.equals("naive")) {
            return new NaiveServerlessChunkLoadingPolicy(world);
        } else if (name.equals("async")) {
            return new AsyncServerlessChunkLoadingPolicy(world);
        }

        return new DefaultChunkLoadingPolicy(world);
    }

    /**
     * Called every tick
     *
     * @param players player list
     */
    public void update(Collection<GlowPlayer> players, Messaging messagingSystem) {
        updateQOS(players);
    }

    /**
     * Monitors the playability of the game by calculating the distance from each player to the closest unpopulated chunk.
     *
     * @param players list of players
     */
    private void updateQOS(Collection<GlowPlayer> players) {
        if (players.size() == 0) {
            return;
        }

        qos = 0;
        qos_min = Double.MAX_VALUE;
        for (GlowPlayer player : players) {
            double dist = getDistanceToUnpopulated(player);
            qos += dist;
            qos_min = Math.min(qos_min, dist);
        }

        // normalize
        qos /= players.size();
        GlowServer.eventLogger.log("QOS", qos);
        GlowServer.eventLogger.log("QOS_MIN", qos_min);
    }

    /**
     * Calculates the distance (measured in blocks) to the closest unpopulated chunk
     *
     * @param player the player to calculate the distance for
     * @return the distance to the chunk
     */
    private double getDistanceToUnpopulated(GlowPlayer player) {
        AreaOfInterest areaOfInterest = player.getAreaOfInterest();
        double minDist = player.getViewDistance() * 16;

        for (GlowChunk chunk : areaOfInterest) {
            if (chunk.isPopulated()) {
                continue;
            }

            // calculate distance from player to middle of chunk
            minDist = Math.min(minDist, player.getCoordinates().distance(chunk.getCenterCoordinates()));
        }

        return minDist;
    }

    /**
     * Called on world shutdown
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Triggers population of a chunk.
     *
     * @param player The player that requested the chunk data.
     * @param chunk  The chunk to populate
     */
    public abstract void triggerChunkPopulation(GlowPlayer player, GlowChunk chunk);

    /**
     * Find the chunks that are in the first area of interest, but not in the second.
     *
     * @param first  the first area of interest.
     * @param second the second area of interest.
     * @return the list of chunks.
     */
    List<ChunkRunnable> getDifference(GlowPlayer player, AreaOfInterest first, AreaOfInterest second) {

        if (first == null || first.equals(second)) {
            return new ArrayList<>();
        }

        if (second == null || first.getWorld() != second.getWorld()) {
            List<ChunkRunnable> runnables = new ArrayList<>();
            first.forEach(chunk -> {
                ChunkRunnable runnable = new ChunkRunnable(player, chunk);
                runnables.add(runnable);
            });
            return runnables;
        }

        List<ChunkRunnable> runnables = new ArrayList<>();
        first.forEach(chunk -> {
            if (!second.contains(chunk)) {
                ChunkRunnable runnable = new ChunkRunnable(player, chunk);
                runnables.add(runnable);
            }
        });
        return runnables;
    }

    /**
     * Tell players to unload all the given chunks, except for the ones that have been cancelled. Because, the ones
     * that were cancelled were never streamed to the player.
     *
     * @param toUnload The chunks to be unloaded.
     */
    void unloadChunks(List<ChunkRunnable> toUnload) {
        toUnload.forEach(runnable -> {

            GlowPlayer player = runnable.getPlayer();
            GlowChunk chunk = runnable.getChunk();

            Message message = new UnloadChunkMessage(chunk.getX(), chunk.getZ());
            Session session = player.getSession();
            session.send(message);

            ChunkManager.ChunkLock lock = player.getChunkLock();
            lock.release(chunk.getCoordinates());
        });
    }
}
