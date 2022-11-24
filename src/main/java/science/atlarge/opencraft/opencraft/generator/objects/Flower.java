package science.atlarge.opencraft.opencraft.generator.objects;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import science.atlarge.opencraft.opencraft.block.GlowBlock;

import java.util.Random;

public class Flower implements TerrainObject {

    private final Material type;
    private final int data;

    public Flower(FlowerType plantType) {
        type = plantType.getType();
        data = plantType.getData();
    }

    /**
     * Generates up to 64 flowers around the given point.
     */
    @Override
    public boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        boolean succeeded = false;
        for (int i = 0; i < 64; i++) {
            int x = sourceX + random.nextInt(8) - random.nextInt(8);
            int z = sourceZ + random.nextInt(8) - random.nextInt(8);
            int y = sourceY + random.nextInt(4) - random.nextInt(4);

            GlowBlock block = (GlowBlock) world.getBlockAt(x, y, z);
            if (y < 255 && block.getType() == Material.AIR
                    && block.getRelative(BlockFace.DOWN).getType() == Material.GRASS) {
                block.setTypeNoBroadcast(type);
                block.setDataNoBroadcast((byte) data);
                succeeded = true;
            }
        }
        return succeeded;
    }
}
