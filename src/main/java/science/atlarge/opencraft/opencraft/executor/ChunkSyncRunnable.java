package science.atlarge.opencraft.opencraft.executor;

import com.flowpowered.network.Message;
import org.bukkit.World;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.chunk.GlowChunk;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.net.GlowSession;
import science.atlarge.opencraft.opencraft.population.PopulateInfo;
import science.atlarge.opencraft.opencraft.util.IntCoordinates2D;

import java.util.Objects;

/**
 * This class is class responsible for generating chunks and sending the chunk data to a player. This class has a
 * priority that is used for executing ChunkRunnables in a priority based order. The order is normally determined by the
 * distance between the chunk and the player. So chunks closer to the player will be prioritized over chunks further
 * away from the player.
 */
public class ChunkSyncRunnable extends ChunkRunnable {

    private final PopulateInfo.PopulateOutput chunkData;

    /**
     * Construct a ChunkRunnable for a chunk that the given player needs to receive the data for.
     *
     * @param player The player to which the chunk data will be sent to.
     * @param chunk  The chunk for which the data needs to be sent to the player.
     */
    public ChunkSyncRunnable(GlowPlayer player, GlowChunk chunk, PopulateInfo.PopulateOutput chunkData) {
        super(player, chunk);
        this.chunkData = chunkData;
    }

    @Override
    public void run() {
        GlowSession session = player.getSession();
        GlowWorld world = chunk.getWorld();
        int x = chunk.getX();
        int z = chunk.getZ();
        boolean skylight = world.getEnvironment() == World.Environment.NORMAL;

        // sync the chunk to memory
        world.getChunkManager().syncChunk(chunk.getCoordinates(), chunkData);

        // log player position after population
        GlowServer.eventLogger.log(String.format("received_chunk (%d,%d)", x, z),
                String.format("(%d,%d)", player.getLocation().getBlockX(), player.getLocation().getBlockZ()));

        // send the chunk to the player
        IntCoordinates2D coordinates = new IntCoordinates2D(x, z);
        player.getChunkLock().acquire(coordinates);

        Message message = chunk.toMessage(skylight);
        session.send(message);

        chunk.getRawBlockEntities().forEach(entity -> entity.update(player));
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        ChunkRunnable other = (ChunkSyncRunnable) object;
        return Objects.equals(player, other.player) && Objects.equals(chunk, other.chunk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, chunk);
    }
}
