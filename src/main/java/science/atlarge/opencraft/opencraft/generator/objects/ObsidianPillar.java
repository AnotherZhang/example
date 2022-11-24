package science.atlarge.opencraft.opencraft.generator.objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.block.GlowBlock;

import java.util.Random;

public class ObsidianPillar implements TerrainObject {

    @Override
    public boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        if (!world.getBlockAt(sourceX, sourceY, sourceZ).isEmpty()
                || world.getBlockAt(sourceX, sourceY - 1, sourceZ).getType()
                != Material.ENDER_STONE) {
            return false;
        }

        int height = random.nextInt(32) + 6;
        int radius = random.nextInt(4) + 1;

        // check under the pillar that there's no gap
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (i * i + j * j <= radius * radius + 1
                        && world.getBlockAt(sourceX + i, sourceY - 1, sourceZ + j).getType()
                        != Material.ENDER_STONE) {
                    return false;
                }
            }
        }

        // build a pillar
        for (int k = 0; k < height && sourceY + k < 256; k++) {
            for (int i = -radius; i <= radius; i++) {
                for (int j = -radius; j <= radius; j++) {
                    if (i * i + j * j <= radius * radius + 1) {
                        GlowBlock block = (GlowBlock) world.getBlockAt(sourceX + i, sourceY + k, sourceZ + j);
                        block.setTypeNoBroadcast(Material.OBSIDIAN);
                    }
                }
            }
        }

        final Location loc = new Location(world, sourceX + 0.5D, sourceY + height + 1,
                sourceZ + 0.5D, random.nextFloat() * 360, 0);
        world.spawnEntity(loc, EntityType.ENDER_CRYSTAL);
        GlowBlock block = (GlowBlock) world.getBlockAt(sourceX, sourceY + height, sourceZ);
        block.setTypeNoBroadcast(Material.BEDROCK);
        return true;
    }
}
