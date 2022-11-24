package science.atlarge.opencraft.opencraft.generator.populators;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.generator.structures.GlowStructure;
import science.atlarge.opencraft.opencraft.io.structure.StructureStorage;
import science.atlarge.opencraft.opencraft.io.structure.StructureStore;
import science.atlarge.opencraft.opencraft.util.BlockStateDelegate;
import science.atlarge.opencraft.opencraft.util.IntCoordinates2D;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class StructurePopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {

        if (world.canGenerateStructures()) {

            int cx = source.getX();
            int cz = source.getZ();

            random.setSeed(world.getSeed());
            long randX = random.nextLong();
            long randZ = random.nextLong();

            boolean placed = false;
            for (int x = cx - 8; x <= cx + 8 && !placed; x++) {
                for (int z = cz - 8; z <= cz + 8 && !placed; z++) {
                    if (!world.getChunkAt(x, z).isLoaded() && !world.getChunkAt(x, z).load(true)) {
                        continue;
                    }
                    random.setSeed(x * randX + z * randZ ^ world.getSeed());
                    Map<IntCoordinates2D, GlowStructure> structures = ((GlowWorld) world)
                            .getStructures();
                    IntCoordinates2D chunkCoordinates = new IntCoordinates2D(x, z);
                    if (structures.containsKey(chunkCoordinates)) {
                        continue;
                    }
                    for (StructureStore<?> store : StructureStorage.getStructureStores()) {
                        GlowStructure structure = store
                                .createNewStructure((GlowWorld) world, random, x, z);
                        if (structure.shouldGenerate(random)) {
                            structure.setDirty(true);
                            structures.put(chunkCoordinates, structure);
                            GlowServer.logger.finer("structure in chunk " + x + "," + z);
                            placed = true;
                            break;
                        }
                    }
                }
            }

            int x = cx << 4;
            int z = cz << 4;
            Iterator<Entry<IntCoordinates2D, GlowStructure>> it = ((GlowWorld) world).getStructures()
                    .entrySet().iterator();
            while (it.hasNext()) {
                GlowStructure structure = it.next().getValue();
                if (structure.getBoundingBox().intersectsWith(x, z, x + 15, z + 15)) {
                    BlockStateDelegate delegate = new BlockStateDelegate();
                    if (structure.generate(random, x, z, delegate)) {
                        // maybe later trigger a StructureGeneratedEvent event and cancel
                        delegate.updateBlockStatesNoBroadcast();
                    } else {
                        delegate.rollbackBlockStatesNoBroadcast();
                        it.remove();
                    }
                }
            }
        }
    }
}
