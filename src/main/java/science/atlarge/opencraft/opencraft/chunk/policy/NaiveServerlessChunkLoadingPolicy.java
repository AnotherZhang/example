package science.atlarge.opencraft.opencraft.chunk.policy;

import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.chunk.ChunkManager;
import science.atlarge.opencraft.opencraft.chunk.GlowChunk;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.executor.ChunkSyncRunnable;
import science.atlarge.opencraft.opencraft.executor.PriorityExecutor;
import science.atlarge.opencraft.opencraft.measurements.EventLogger;
import science.atlarge.opencraft.opencraft.messaging.Messaging;
import science.atlarge.opencraft.opencraft.population.PopulateInfo;
import science.atlarge.opencraft.opencraft.population.PopulationInvoker;
import science.atlarge.opencraft.opencraft.population.PopulationInvokerAzure;
import science.atlarge.opencraft.opencraft.util.config.ServerConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class NaiveServerlessChunkLoadingPolicy extends DefaultChunkLoadingPolicy {

    private final PriorityExecutor<ChunkSyncRunnable> syncExecutor;

    public NaiveServerlessChunkLoadingPolicy(GlowWorld world) {
        super(world);
        syncExecutor = new PriorityExecutor<>(1); // run on single thread
    }

    @Override
    public void update(Collection<GlowPlayer> players, Messaging messagingSystem) {
        super.update(players, messagingSystem);

        // the sync queue's priority is only updated when calling execute and cancel
        syncExecutor.executeAndCancel(new ArrayList<>(), ChunkSyncRunnable::shouldBeCancelled);
    }

    @Override
    public void triggerChunkPopulation(GlowPlayer player, GlowChunk chunk) {
        ChunkManager chunkManager = world.getChunkManager();
        EventLogger logger = GlowServer.eventLogger;

        int x = chunk.getX();
        int z = chunk.getZ();

        // start serverless chunk population timer
        logger.start(String.format("serverless_population (%d,%d)", x, z));

        try {
            // try to load chunk from file first
            chunk.load(false);

            // cancel out if it's already populated
            if (chunk.isPopulated()) {
                logger.cancel(String.format("serverless_population (%d,%d)", x, z));
                return;
            }

            String functionName = world.getServer().getConfig().getString(ServerConfig.Key.OPENCRAFT_CHUNK_POPULATION_FUNCTION);
            String provider = world.getServer().getConfig().getString(ServerConfig.Key.OPENCRAFT_CHUNK_POPULATION_PROVIDER);

            // invoke the lambda function
            PopulateInfo.PopulateOutput output = null;
            switch (provider) {
                case "azure":
                     output = PopulationInvokerAzure.invoke(
                        new PopulateInfo.PopulateInput(world, x, z), GlowServer.eventLogger
                    );
                    break;
                case "aws":
                    output = PopulationInvoker.invoke(
                        new PopulateInfo.PopulateInput(world, x, z), functionName, GlowServer.eventLogger
                    );
            }

            if (output == null) {
                // Cancel chunk population timer
                logger.cancel(String.format("serverless_population (%d,%d)", x, z));

                GlowServer.logger.info(String.format("Serverless population for chunk (%d,%d) failed", x, z));

                // Populate chunk locally
                chunkManager.forcePopulation(chunk.getCoordinates());
            } else {
                // add chunk to sync queue
                ChunkSyncRunnable syncRunnable = new ChunkSyncRunnable(player, chunk, output);
                List<ChunkSyncRunnable> toExecute = Collections.singletonList(syncRunnable);
                Set<ChunkSyncRunnable> cancelled = syncExecutor.executeAndCancel(toExecute, ChunkSyncRunnable::shouldBeCancelled);

                // log chunk added to queue and cancelled chunks
                logger.start(String.format("sync_queue (%d,%d)", x, z));
                for (ChunkSyncRunnable ch : cancelled) {
                    logger.cancel(String.format("sync_queue (%d,%d)", ch.getChunk().getX(), ch.getChunk().getZ()));
                }
            }
        } catch (Exception ex) {
            // Cancel chunk population timer
            logger.cancel(String.format("serverless_population (%d,%d)", x, z));

            GlowServer.logger.info(String.format("Serverless population for chunk (%d,%d) failed", x, z));

            // Populate chunk locally
            chunkManager.forcePopulation(chunk.getCoordinates());
        } finally {
            // stop serverless chunk population timer and mark as success
            logger.stop(String.format("serverless_population (%d,%d)", x, z));
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        syncExecutor.shutdown();
    }
}
