package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;

/**
 * A redstone lamp.
 *
 * @author Sam
 */
public class BlockLamp extends BlockType {

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
            GlowBlockState oldState) {
        updatePhysics(block);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
            Material oldType, byte oldData, Material newType, byte newData) {
        updatePhysics(block);
    }

    @Override
    public void updatePhysicsAfterEvent(GlowBlock me) {
        super.updatePhysicsAfterEvent(me);
        boolean powered = me.isBlockPowered() || me.isBlockIndirectlyPowered();

        if (powered != (me.getType() == Material.REDSTONE_LAMP_ON)) {
            me.setType(powered ? Material.REDSTONE_LAMP_ON : Material.REDSTONE_LAMP_OFF);
        }
    }
}
