package science.atlarge.opencraft.opencraft.executor;

import com.flowpowered.network.Message;
import org.bukkit.World;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.chunk.AreaOfInterest;
import science.atlarge.opencraft.opencraft.chunk.GlowChunk;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.net.GlowSession;
import science.atlarge.opencraft.opencraft.util.Coordinates;
import science.atlarge.opencraft.opencraft.util.IntCoordinates2D;

import java.util.Objects;

/**
 * This class is class responsible for generating chunks and sending the chunk data to a player. This class has a
 * priority that is used for executing ChunkRunnables in a priority based order. The order is normally determined by the
 * distance between the chunk and the player. So chunks closer to the player will be prioritized over chunks further
 * away from the player.
 */
public class ChunkRunnable extends PriorityRunnable {

    protected final GlowPlayer player;
    protected final GlowChunk chunk;

    /**
     * Construct a ChunkRunnable for a chunk that the given player needs to receive the data for.
     *
     * @param player The player to which the chunk data will be sent to.
     * @param chunk  The chunk for which the data needs to be sent to the player.
     */
    public ChunkRunnable(GlowPlayer player, GlowChunk chunk) {
        this.player = player;
        this.chunk = chunk;
        updatePriority();
    }

    /**
     * Get the chunk for which the data needs to be sent to the player.
     *
     * @return The chunk for which the data needs to be sent to the player.
     */
    public GlowChunk getChunk() {
        return chunk;
    }

    /**
     * Get the player whom should receive the chunk data.
     *
     * @return The player whom should receive the chunk data.
     */
    public GlowPlayer getPlayer() {
        return player;
    }

    /**
     * Check whether the chunk runnable should be cancelled. The decision is made based on the distance between the
     * server's view distance, the player's view distance and position, and the chunk's position.
     *
     * @return whether the runnable should be cancelled.
     */
    public boolean shouldBeCancelled() {
        AreaOfInterest area = player.getAreaOfInterest();
        return !area.contains(chunk);
    }

    /**
     * Update the priority of the ChunkRunnable. This is computed by calculating the distance between the center of the
     * chunk the player.
     */
    @Override
    public void updatePriority() {
        Coordinates chunkCenter = chunk.getCenterCoordinates();
        Coordinates playerCoordinates = player.getCoordinates();
        double squaredDistance = chunkCenter.squaredDistance(playerCoordinates);
        setPriority(squaredDistance);
    }

    @Override
    public void run() {

        GlowSession session = player.getSession();
        GlowWorld world = chunk.getWorld();
        int x = chunk.getX();
        int z = chunk.getZ();
        boolean skylight = world.getEnvironment() == World.Environment.NORMAL;

        if (!chunk.isPopulated()) {
            // signal that the chunk is no longer in the queue
            GlowServer.eventLogger.stop(String.format("queue (%d,%d)", chunk.getX(), chunk.getZ()));
            world.getChunkLoadingPolicy().triggerChunkPopulation(player, chunk);

            if (chunk.isPopulated()) {
                // only record the distance to the player after population
                GlowServer.eventLogger.log(String.format("received_chunk (%d,%d)", x, z),
                        String.format("(%d,%d)", player.getLocation().getBlockX(), player.getLocation().getBlockZ()));
            }
        }

        if (chunk.isPopulated()) {
            IntCoordinates2D coordinates = new IntCoordinates2D(x, z);
            // Calling this adds the coordinates to the lockSet in the ChunkManager and prevents the chunks being
            // unloaded
            player.getChunkLock().acquire(coordinates);

            Message message = chunk.toMessage(skylight);
            // TODO (all) messages should be sent via the messaging system.
            session.send(message);

            chunk.getRawBlockEntities().forEach(entity -> entity.update(player));
        }
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        ChunkRunnable other = (ChunkRunnable) object;
        return Objects.equals(player, other.player) && Objects.equals(chunk, other.chunk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, chunk);
    }
}
