package science.atlarge.opencraft.opencraft.generator.decorators.nether;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.generator.decorators.BlockDecorator;

import java.util.Random;

public class GlowstoneDecorator extends BlockDecorator {

    private static final BlockFace[] SIDES = new BlockFace[] {BlockFace.EAST, BlockFace.WEST,
            BlockFace.DOWN, BlockFace.UP, BlockFace.SOUTH, BlockFace.NORTH};

    private boolean variableAmount;

    public GlowstoneDecorator() {
        this(false);
    }

    public GlowstoneDecorator(boolean variableAmount) {
        this.variableAmount = variableAmount;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int amount = variableAmount ? 1 + random.nextInt(1 + random.nextInt(10)) : 10;
        for (int i = 0; i < amount; i++) {

            int sourceX = (source.getX() << 4) + random.nextInt(16);
            int sourceZ = (source.getZ() << 4) + random.nextInt(16);
            int sourceY = 4 + random.nextInt(120);

            Block block = world.getBlockAt(sourceX, sourceY, sourceZ);
            if (!block.isEmpty()
                    || block.getRelative(BlockFace.UP).getType() != Material.NETHERRACK) {
                continue;
            }
            GlowBlockState state = (GlowBlockState) block.getState();
            state.setType(Material.GLOWSTONE);
            state.updateNoBroadcast(true, true);

            for (int j = 0; j < 1500; j++) {
                int x = sourceX + random.nextInt(8) - random.nextInt(8);
                int z = sourceZ + random.nextInt(8) - random.nextInt(8);
                int y = sourceY - random.nextInt(12);
                block = world.getBlockAt(x, y, z);
                if (!block.isEmpty()) {
                    continue;
                }
                int glowstoneBlockCount = 0;
                for (BlockFace face : SIDES) {
                    if (block.getRelative(face).getType() == Material.GLOWSTONE) {
                        glowstoneBlockCount++;
                    }
                }
                if (glowstoneBlockCount == 1) {
                    state = (GlowBlockState) block.getState();
                    state.setType(Material.GLOWSTONE);
                    state.updateNoBroadcast(true, true);
                }
            }
        }
    }
}
