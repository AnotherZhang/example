package science.atlarge.opencraft.opencraft.chunk.policy;

import com.amazonaws.services.lambda.model.InvokeResult;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.chunk.GlowChunk;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.executor.ChunkAsyncRunnable;
import science.atlarge.opencraft.opencraft.executor.PriorityExecutor;
import science.atlarge.opencraft.opencraft.measurements.EventLogger;
import science.atlarge.opencraft.opencraft.messaging.Messaging;
import science.atlarge.opencraft.opencraft.population.PopulateInfo;
import science.atlarge.opencraft.opencraft.population.PopulationAsyncInvoker;
import science.atlarge.opencraft.opencraft.util.config.ServerConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class AsyncServerlessChunkLoadingPolicy extends DefaultChunkLoadingPolicy {

    /**
     * Single-threaded executor that syncs chunks to memory
     */
    private final PriorityExecutor<ChunkAsyncRunnable> syncExecutor;

    private final PopulationAsyncInvoker invoker;

    /**
     * Set of made requests that are awaiting completion
     */
    private final Set<ChunkAsyncRunnable> uncompletedRequests;

    public AsyncServerlessChunkLoadingPolicy(GlowWorld world) {
        super(world);

        syncExecutor = new PriorityExecutor<>(1); // sync on single thread
        invoker = new PopulationAsyncInvoker();
        uncompletedRequests = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void update(Collection<GlowPlayer> players, Messaging messagingSystem) {
        super.update(players, messagingSystem);

        EventLogger logger = GlowServer.eventLogger;
        List<ChunkAsyncRunnable> toSync = new ArrayList<>();
        // add the completed requests to the sync queue
        for (ChunkAsyncRunnable ch : uncompletedRequests) {
            if (ch.isDone() && !ch.shouldBeCancelled()) {
                toSync.add(ch);
                logger.stop(String.format("serverless_population (%d,%d)", ch.getChunk().getX(), ch.getChunk().getZ()));
                logger.start(String.format("sync_queue (%d,%d)", ch.getChunk().getX(), ch.getChunk().getZ()));
            }
        }

        // remove the completed requests from the hashset
        uncompletedRequests.removeAll(uncompletedRequests.stream().filter(ChunkAsyncRunnable::isDone).collect(Collectors.toList()));

        // add the completed requests to the sync queue
        syncExecutor.executeAndCancel(toSync, ChunkAsyncRunnable::shouldBeCancelled);
    }

    @Override
    public void triggerChunkPopulation(GlowPlayer player, GlowChunk chunk) {
        int x = chunk.getX();
        int z = chunk.getZ();

        // try to load chunk from file first
        chunk.load(false);

        // cancel out if it's already populated
        if (chunk.isPopulated()) {
            return;
        }

        String functionName = world.getServer().getConfig().getString(ServerConfig.Key.OPENCRAFT_CHUNK_POPULATION_FUNCTION);

        GlowServer.eventLogger.start(String.format("serverless_population (%d,%d)", x, z));
        // invoke the lambda function
        Future<InvokeResult> result = invoker.invoke(
                new PopulateInfo.PopulateInput(world, x, z), functionName, GlowServer.eventLogger
        );

        // add chunk to sync queue
        uncompletedRequests.add(new ChunkAsyncRunnable(player, chunk, result));
    }

    @Override
    public void shutdown() {
        super.shutdown();
        syncExecutor.shutdown();
    }
}
