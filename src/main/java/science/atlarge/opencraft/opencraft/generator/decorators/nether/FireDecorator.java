package science.atlarge.opencraft.opencraft.generator.decorators.nether;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.generator.decorators.BlockDecorator;

import java.util.Random;

public class FireDecorator extends BlockDecorator {

    @Override
    public void decorate(World world, Random random, Chunk source) {

        int amount = 1 + random.nextInt(1 + random.nextInt(10));
        for (int j = 0; j < amount; j++) {
            int sourceX = (source.getX() << 4) + random.nextInt(16);
            int sourceZ = (source.getZ() << 4) + random.nextInt(16);
            int sourceY = 4 + random.nextInt(120);

            for (int i = 0; i < 64; i++) {
                int x = sourceX + random.nextInt(8) - random.nextInt(8);
                int z = sourceZ + random.nextInt(8) - random.nextInt(8);
                int y = sourceY + random.nextInt(4) - random.nextInt(4);

                Block block = world.getBlockAt(x, y, z);
                Block blockBelow = world.getBlockAt(x, y - 1, z);
                if (y < 128 && block.getType() == Material.AIR
                        && blockBelow.getType() == Material.NETHERRACK) {
                    GlowBlockState state = (GlowBlockState) block.getState();
                    state.setType(Material.FIRE);
                    state.setData(new MaterialData(Material.FIRE));
                    state.updateNoBroadcast(true, true);
                }
            }
        }
    }
}
