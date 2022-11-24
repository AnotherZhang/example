package science.atlarge.opencraft.opencraft.generator.decorators.overworld;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import science.atlarge.opencraft.opencraft.block.GlowBlock;

import java.util.Random;

public class EmeraldOreDecorator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        int sourceX = chunk.getX() << 4;
        int sourceZ = chunk.getZ() << 4;

        for (int i = 0; i < random.nextInt(6) + 3; i++) {
            int x = sourceX + random.nextInt(16);
            int z = sourceZ + random.nextInt(16);
            int y = random.nextInt(28) + 4;

            if (world.getBlockAt(x, y, z).getType() == Material.STONE) {
                ((GlowBlock) world.getBlockAt(x, y, z)).setTypeNoBroadcast(Material.EMERALD_ORE);
            }
        }
    }
}
