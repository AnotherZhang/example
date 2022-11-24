package science.atlarge.opencraft.opencraft.io.nbt;

import org.bukkit.Server;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.ServerProvider;
import science.atlarge.opencraft.opencraft.generator.structures.GlowStructure;
import science.atlarge.opencraft.opencraft.i18n.ConsoleMessages;
import science.atlarge.opencraft.opencraft.io.StructureDataService;
import science.atlarge.opencraft.opencraft.io.structure.StructureStorage;
import science.atlarge.opencraft.opencraft.io.structure.StructureStore;
import science.atlarge.opencraft.opencraft.util.IntCoordinates2D;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;
import science.atlarge.opencraft.opencraft.util.nbt.NbtInputStream;
import science.atlarge.opencraft.opencraft.util.nbt.NbtOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NbtStructureDataService implements StructureDataService {

    private final GlowWorld world;
    private final File structureDir;
    private final Server server;

    /**
     * Creates the instance for the given world's structures.
     *
     * @param world        the world
     * @param structureDir the world's structure-data folder, which is created if it doesn't exist
     */
    public NbtStructureDataService(GlowWorld world, File structureDir) {
        this.world = world;
        this.structureDir = structureDir;
        server = ServerProvider.getServer();

        if (!structureDir.isDirectory() && !structureDir.mkdirs()) {
            ConsoleMessages.Warn.Io.MKDIR_FAILED.log(structureDir);
        }
    }

    @Override
    public Map<IntCoordinates2D, GlowStructure> readStructuresData() {
        Map<IntCoordinates2D, GlowStructure> structures = new HashMap<>();
        for (StructureStore<?> store : StructureStorage.getStructureStores()) {
            File structureFile = new File(structureDir, store.getId() + ".dat");
            if (structureFile.exists()) {
                try (NbtInputStream in = new NbtInputStream(new FileInputStream(structureFile))) {
                    CompoundTag data = in.readCompound();
                    if (!data.readCompound("data", innerData -> innerData.readCompound(// NON-NLS
                            "Features", features -> features.getValue().keySet().stream() // NON-NLS
                                    .filter(features::isCompound)
                                    .forEach(key -> {
                                        GlowStructure structure = StructureStorage
                                                .loadStructure(world, features.getCompound(key));
                                        structures.put(new IntCoordinates2D(structure.getChunkX(), structure.getChunkZ()), structure);
                                    })))) {
                        ConsoleMessages.Error.Structure.NO_DATA.log(structureFile);
                    }
                } catch (IOException e) {
                    ConsoleMessages.Error.Structure.LOAD_FAILED.log(e, structureFile);
                }
            }
        }
        return structures;
    }

    @Override
    public void writeStructuresData(Map<IntCoordinates2D, GlowStructure> structures) {
        for (GlowStructure structure : structures.values()) {
            if (structure.isDirty()) {
                CompoundTag data;
                CompoundTag features;
                CompoundTag feature = new CompoundTag();
                CompoundTag inputRoot;
                StructureStore<GlowStructure> store = StructureStorage
                        .saveStructure(structure, feature);
                File structureFile = new File(structureDir, store.getId() + ".dat");
                if (structureFile.exists()) {
                    try (NbtInputStream in = new NbtInputStream(
                            new FileInputStream(structureFile))) {
                        inputRoot = in.readCompound();
                        data = inputRoot.tryGetCompound("data") // NON-NLS
                                .orElseGet(CompoundTag::new);
                    } catch (IOException e) {
                        ConsoleMessages.Error.Structure.LOAD_FAILED.log(e, structureFile);
                        data = new CompoundTag();
                    }
                    features = data.tryGetCompound("Features") // NON-NLS
                            .orElseGet(CompoundTag::new);
                } else {
                    data = new CompoundTag();
                    features = new CompoundTag();
                }
                String key = "[" + structure.getChunkX() + "," + structure.getChunkZ() + "]";
                features.putCompound(key, feature);
                data.putCompound("Features", features); // NON-NLS
                CompoundTag root = new CompoundTag();
                root.putCompound("data", data); // NON-NLS
                try (NbtOutputStream nbtOut = new NbtOutputStream(
                        new FileOutputStream(structureFile))) {
                    nbtOut.writeTag(root);
                } catch (IOException e) {
                    ConsoleMessages.Error.Structure.SAVE_FAILED.log(e, structureFile);
                }
                structure.setDirty(false);
            }
        }
    }
}
