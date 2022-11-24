package science.atlarge.opencraft.opencraft.io;

import science.atlarge.opencraft.opencraft.generator.structures.GlowStructure;
import science.atlarge.opencraft.opencraft.util.IntCoordinates2D;

import java.util.Map;

/**
 * Provider of I/O for structures data.
 */
public interface StructureDataService {

    /**
     * Reads the structures data from storage.
     *
     * @return A map containing structures indexed by their chunk hash.
     */
    Map<IntCoordinates2D, GlowStructure> readStructuresData();

    /**
     * Write the structures data to storage.
     *
     * @param structures The structures to write to storage.
     */
    void writeStructuresData(Map<IntCoordinates2D, GlowStructure> structures);
}
