package science.atlarge.opencraft.opencraft.generator.decorators.nether;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.generator.decorators.BlockDecorator;
import science.atlarge.opencraft.opencraft.scheduler.PulseTask;

import java.util.Random;

public class LavaDecorator extends BlockDecorator {

    private static final BlockFace[] SIDES = new BlockFace[] {BlockFace.NORTH, BlockFace.EAST,
            BlockFace.SOUTH, BlockFace.WEST, BlockFace.DOWN};

    private boolean flowing;

    public LavaDecorator() {
        this(false);
    }

    public LavaDecorator(boolean flowing) {
        this.flowing = flowing;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = flowing ? 4 + random.nextInt(120) : 10 + random.nextInt(108);

        Block block = world.getBlockAt(sourceX, sourceY, sourceZ);
        if ((block.getType() != Material.NETHERRACK && !block.isEmpty())
                || block.getRelative(BlockFace.UP).getType() != Material.NETHERRACK) {
            return;
        }
        int netherrackBlockCount = 0;
        int airBlockCount = 0;
        for (BlockFace face : SIDES) {
            Block neighbor = block.getRelative(face);
            if (neighbor.getType() == Material.NETHERRACK) {
                netherrackBlockCount++;
            } else if (neighbor.isEmpty()) {
                airBlockCount++;
            }
        }

        if (netherrackBlockCount == 5
                || flowing && airBlockCount == 1 && netherrackBlockCount == 4) {
            GlowBlockState state = (GlowBlockState) block.getState();
            state.setType(Material.LAVA);
            state.updateNoBroadcast(true, true);
            new PulseTask((GlowBlock) block, true, 1, true).startPulseTask();
        }
    }
}
