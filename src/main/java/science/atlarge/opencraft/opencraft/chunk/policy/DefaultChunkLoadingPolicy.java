package science.atlarge.opencraft.opencraft.chunk.policy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.chunk.AreaOfInterest;
import science.atlarge.opencraft.opencraft.chunk.GlowChunk;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.executor.ChunkRunnable;
import science.atlarge.opencraft.opencraft.messaging.Messaging;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultChunkLoadingPolicy extends ChunkLoadingPolicy {

    public DefaultChunkLoadingPolicy(GlowWorld world) {
        super(world);
    }

    @Override
    public void update(Collection<GlowPlayer> players, Messaging messagingSystem) {
        super.update(players, messagingSystem);

        ImmutableMap.Builder<GlowPlayer, AreaOfInterest> currentAreasBuilder = ImmutableMap.builder();
        players.forEach(player -> currentAreasBuilder.put(player, player.getAreaOfInterest()));
        ImmutableMap<GlowPlayer, AreaOfInterest> currentAreas = currentAreasBuilder.build();

        Set<GlowPlayer> currentPlayers = currentAreas.keySet();
        Set<GlowPlayer> previousPlayers = previousAreas.keySet();
        Sets.SetView<GlowPlayer> allPlayers = Sets.union(currentPlayers, previousPlayers);

        world.getServer().getScheduler().startMeasurement("dyconit_policy_update_" + world.getName(), "Time spent updating dyconit subscriptions");
        messagingSystem.globalUpdate();
        allPlayers.forEach(messagingSystem::update);
        world.getServer().getScheduler().stopMeasurement("dyconit_policy_update_" + world.getName());

        List<ChunkRunnable> chunksToLoad = allPlayers.parallelStream()
                .map(player -> {
                    AreaOfInterest current = currentAreas.get(player);
                    AreaOfInterest previous = previousAreas.get(player);
                    return getChunksToLoad(player, current, previous);
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // log the chunks we just put in the queue
        for (ChunkRunnable ch : chunksToLoad) {
            GlowServer.eventLogger.start(String.format("queue (%d,%d)", ch.getChunk().getX(), ch.getChunk().getZ()));
        }

        Set<ChunkRunnable> cancelled = executor.executeAndCancel(chunksToLoad, ChunkRunnable::shouldBeCancelled);

        // remove the cancelled chunks from logging
        for (ChunkRunnable ch : cancelled) {
            GlowServer.eventLogger.cancel(String.format("queue (%d,%d)", ch.getChunk().getX(), ch.getChunk().getZ()));
        }

        List<ChunkRunnable> chunksToUnload = allPlayers.parallelStream()
                .map(player -> {
                    AreaOfInterest current = currentAreas.get(player);
                    AreaOfInterest previous = previousAreas.get(player);
                    return getChunksToUnload(player, current, previous);
                })
                .flatMap(List::stream)
                .filter(runnable -> !cancelled.contains(runnable))
                .collect(Collectors.toList());

        unloadChunks(chunksToUnload);

        previousAreas = currentAreas;
    }

    @Override
    public void triggerChunkPopulation(GlowPlayer player, GlowChunk chunk) {
        world.getChunkManager().forcePopulation(chunk.getCoordinates());
    }

    /**
     * List chunks that are in the current area of interest, but not in the previous.
     *
     * @param current  the current area of interest.
     * @param previous the previous area of interest.
     * @return the list of chunks.
     */
    List<ChunkRunnable> getChunksToLoad(GlowPlayer player, AreaOfInterest current, AreaOfInterest previous) {
        return getDifference(player, current, previous);
    }

    /**
     * Find chunks that are in the previous area of interest, but not in the current.
     *
     * @param current  the current area of interest.
     * @param previous the previous area of interest.
     * @return the list of chunks.
     */
    List<ChunkRunnable> getChunksToUnload(GlowPlayer player, AreaOfInterest current, AreaOfInterest previous) {
        return getDifference(player, previous, current);
    }
}
