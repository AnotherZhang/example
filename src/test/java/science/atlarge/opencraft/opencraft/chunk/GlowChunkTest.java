package science.atlarge.opencraft.opencraft.chunk;

import org.junit.jupiter.api.Test;
import science.atlarge.opencraft.opencraft.util.Coordinates;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlowChunkTest {

    @Test
    void getCenterCoordinates() {
        final int chunkX = 4;
        final int chunkZ = -3;
        final int centerX = 72;
        final int centerZ = -40;
        final Coordinates coordinates = new Coordinates(centerX, centerZ);

        GlowChunk chunk = new GlowChunk(null, chunkX, chunkZ);

        assertEquals(coordinates, chunk.getCenterCoordinates());
    }

    @Test
    void getCoordinatesChunk() {
        final int chunkX = 4;
        final int chunkZ = -3;

        GlowChunk chunk = new GlowChunk(null, chunkX, chunkZ);

        assertEquals(chunkX, chunk.getCenterCoordinates().getChunkX());
        assertEquals(chunkZ, chunk.getCenterCoordinates().getChunkZ());
    }
}
