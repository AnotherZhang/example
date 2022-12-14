package science.atlarge.opencraft.opencraft.generator.decorators.overworld;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.generator.decorators.BlockDecorator;
import science.atlarge.opencraft.opencraft.population.PopulateInfo;
import science.atlarge.opencraft.opencraft.scheduler.PulseTask;

import java.util.Random;

public class FlowingLiquidDecorator extends BlockDecorator {

    private static final BlockFace[] SIDES = new BlockFace[] {BlockFace.NORTH, BlockFace.EAST,
            BlockFace.SOUTH, BlockFace.WEST};
    private final Material type;

    /**
     * Creates a decorator that carves out waterfalls or lavafalls in stone walls.
     *
     * @param type {@link Material#WATER} or {@link Material#LAVA}
     */
    public FlowingLiquidDecorator(Material type) {
        this.type = type;
        if (type != Material.WATER && type != Material.LAVA) {
            throw new IllegalArgumentException("Flowing liquid must be WATER or LAVA");
        }
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random
                .nextInt(random.nextInt(type == Material.LAVA ? random.nextInt(240) + 8 : 248) + 8);

        Block block = world.getBlockAt(sourceX, sourceY, sourceZ);
        if ((block.getType() == Material.STONE || block.getType() == Material.AIR)
                && block.getRelative(BlockFace.DOWN).getType() == Material.STONE
                && block.getRelative(BlockFace.UP).getType() == Material.STONE) {
            int stoneBlockCount = 0;
            for (BlockFace face : SIDES) {
                if (block.getRelative(face).getType() == Material.STONE) {
                    stoneBlockCount++;
                }
            }
            if (stoneBlockCount == 3) {
                int airBlockCount = 0;
                for (BlockFace face : SIDES) {
                    if (block.getRelative(face).isEmpty()) {
                        airBlockCount++;
                    }
                }
                if (airBlockCount == 1) {
                    GlowBlockState state = (GlowBlockState) block.getState();
                    state.setType(type);
                    state.updateNoBroadcast(true, true);
                    GlowWorld thisWorld = ((GlowBlock) state.getBlock()).getWorld();
                    if (thisWorld.isServerless()) {
                        thisWorld.addPulseTaskInfo(new PopulateInfo.PopulateOutput.PulseTaskInfo(
                                (GlowBlock) state.getBlock(), true, 1, true)
                        );
                    } else {
                        new PulseTask((GlowBlock) state.getBlock(), true, 1, true).startPulseTask();
                    }
                }
            }
        }
    }
}
