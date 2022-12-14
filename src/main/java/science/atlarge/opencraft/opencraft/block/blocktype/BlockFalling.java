package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;

/**
 * Represents a block that falls down, when there's no block below it.
 */
public class BlockFalling extends BlockType {

    private final Material drop;

    public BlockFalling(Material drop) {
        this.drop = drop;
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
            GlowBlockState oldState) {
        updatePhysics(block);
    }

    @Override
    public void onNearBlockChanged(GlowBlock me, BlockFace face, GlowBlock other, Material oldType,
            byte oldData, Material newType, byte newData) {
        for (BlockFace adjacent : ADJACENT) {
            if (adjacent == face) {
                updatePhysics(me);
                break;
            }
        }
    }

    @Override
    public void updatePhysicsAfterEvent(GlowBlock me) {
        super.updatePhysicsAfterEvent(me);
        Block below = me.getRelative(BlockFace.DOWN);
        if (!supportingBlock(below.getType())) {
            //Simulates real Minecraft delay on block fall
            //If possible should be changed to 2.5 ticks
            me.getWorld().getServer().getScheduler()
                    .runTaskLater(null, () -> transformToFallingEntity(me), 2);
        }
    }

    protected void transformToFallingEntity(GlowBlock me) {
        //Force block to update otherwise it can sometimes duplicate
        me = me.getWorld().getBlockAt(me.getX(), me.getY(), me.getZ());

        if (!me.isEmpty()) {
            byte data = me.getData();
            me.setType(Material.AIR);
            me.getWorld().spawnFallingBlock(me.getLocation().add(0.50, 0.00, 0.50), drop, data);
        }
    }

    private boolean supportingBlock(Material material) {
        switch (material) {
            case AIR:
            case FIRE:
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
                return false;
            default:
                return true;
        }
    }
}
