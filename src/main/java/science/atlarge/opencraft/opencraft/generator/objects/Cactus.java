package science.atlarge.opencraft.opencraft.generator.objects;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;

import java.util.Random;

public class Cactus implements TerrainObject {

    private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
            BlockFace.WEST};

    /**
     * Generates or extends a cactus, if there is space.
     */
    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        if (world.getBlockAt(x, y, z).isEmpty()) {
            int height = random.nextInt(random.nextInt(3) + 1) + 1;
            for (int n = y; n < y + height; n++) {
                Block block = world.getBlockAt(x, n, z);
                Material typeBelow = block.getRelative(BlockFace.DOWN).getType();
                if ((typeBelow == Material.SAND || typeBelow == Material.CACTUS)
                        && block.getRelative(BlockFace.UP).isEmpty()) {
                    for (BlockFace face : FACES) {
                        if (block.getRelative(face).getType().isSolid()) {
                            return n > y;
                        }
                    }
                    GlowBlockState state = (GlowBlockState) block.getState();
                    state.setType(Material.CACTUS);
                    state.setData(new MaterialData(Material.CACTUS));
                    state.updateNoBroadcast(true, true);
                }
            }
        }
        return true;
    }
}
