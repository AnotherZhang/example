package science.atlarge.opencraft.opencraft.generator.decorators.overworld;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.material.MaterialData;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.generator.decorators.BlockDecorator;

import java.util.Random;

public class WaterLilyDecorator extends BlockDecorator {

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(world.getHighestBlockYAt(sourceX, sourceZ) << 1);
        while (world.getBlockAt(sourceX, sourceY - 1, sourceZ).getType() == Material.AIR
                && sourceY > 0) {
            sourceY--;
        }

        for (int j = 0; j < 10; j++) {
            int x = sourceX + random.nextInt(8) - random.nextInt(8);
            int z = sourceZ + random.nextInt(8) - random.nextInt(8);
            int y = sourceY + random.nextInt(4) - random.nextInt(4);

            if (y >= 0 && y <= 255 && world.getBlockAt(x, y, z).getType() == Material.AIR
                    && world.getBlockAt(x, y - 1, z).getType() == Material.STATIONARY_WATER) {
                GlowBlockState state = (GlowBlockState) world.getBlockAt(x, y, z).getState();
                state.setType(Material.WATER_LILY);
                state.setData(new MaterialData(Material.WATER_LILY));
                state.updateNoBroadcast(true, true);
            }
        }
    }
}
