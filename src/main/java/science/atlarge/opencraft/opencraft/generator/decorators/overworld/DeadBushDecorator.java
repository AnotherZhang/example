package science.atlarge.opencraft.opencraft.generator.decorators.overworld;

import org.bukkit.Chunk;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.LongGrass;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.generator.decorators.BlockDecorator;

import java.util.Random;

public class DeadBushDecorator extends BlockDecorator {

    private static final Material[] SOIL_TYPES = {Material.SAND, Material.DIRT, Material.HARD_CLAY,
            Material.STAINED_CLAY};

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(world.getHighestBlockYAt(sourceX, sourceZ) << 1);
        while (sourceY > 0
                && (world.getBlockAt(sourceX, sourceY, sourceZ).isEmpty()
                || world.getBlockAt(sourceX, sourceY, sourceZ).getType() == Material.LEAVES)) {
            sourceY--;
        }

        for (int i = 0; i < 4; i++) {
            int x = sourceX + random.nextInt(8) - random.nextInt(8);
            int z = sourceZ + random.nextInt(8) - random.nextInt(8);
            int y = sourceY + random.nextInt(4) - random.nextInt(4);

            if (world.getBlockAt(x, y, z).isEmpty()) {
                Block blockBelow = world.getBlockAt(x, y - 1, z);
                for (Material soil : SOIL_TYPES) {
                    if (soil == blockBelow.getType()) {
                        GlowBlockState state = (GlowBlockState) world.getBlockAt(x, y, z).getState();
                        state.setType(Material.DEAD_BUSH);
                        state.setData(new LongGrass(GrassSpecies.DEAD));
                        state.updateNoBroadcast(true, true);
                        break;
                    }
                }
            }
        }
    }
}
