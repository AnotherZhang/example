package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dispenser;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.block.entity.BlockEntity;
import science.atlarge.opencraft.opencraft.block.entity.DispenserEntity;
import science.atlarge.opencraft.opencraft.block.entity.state.GlowDispenser;
import science.atlarge.opencraft.opencraft.chunk.GlowChunk;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.inventory.MaterialMatcher;
import science.atlarge.opencraft.opencraft.inventory.ToolType;

public class BlockDispenser extends BlockContainer {

    /**
     * Returns the position where an item will emerge from the dispenser.
     *
     * @param block a dispenser block
     * @return the position as a Vector
     */
    public static Vector getDispensePosition(GlowBlock block) {
        BlockFace facing = getFacing(block);
        double x = block.getX() + 0.7 * facing.getModX();
        double y = block.getY() + 0.7 * facing.getModY();
        double z = block.getZ() + 0.7 * facing.getModZ();
        return new Vector(x, y, z);
    }

    /**
     * Returns the direction a dispenser is facing.
     *
     * @param block a dispenser block
     * @return the facing direction
     */
    public static BlockFace getFacing(GlowBlock block) {
        GlowBlockState state = block.getState();
        MaterialData data = state.getData();
        if (!(data instanceof Dispenser)) {
            return BlockFace.SELF;
        }
        Dispenser dispenserData = (Dispenser) data;

        return dispenserData.getFacing();
    }

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new DispenserEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
            ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        MaterialData data = state.getData();
        if (data instanceof Dispenser) {
            ((Dispenser) data).setFacingDirection(getOppositeBlockFace(player.getLocation(), true));
            state.setData(data);
        } else {
            warnMaterialData(Dispenser.class, data);
        }
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }

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
        GlowBlock up = me.getRelative(BlockFace.UP);
        boolean powered = me.isBlockPowered() || me.isBlockIndirectlyPowered()
                || up.isBlockPowered() || up.isBlockIndirectlyPowered();

        GlowBlockState state = me.getState();
        MaterialData data = state.getData();
        if (!(data instanceof Dispenser)) {
            return;
        }

        boolean isTriggered = (data.getData() >> 3 & 1) != 0;
        if (powered && !isTriggered) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    trigger(me);
                }
            }.runTaskLater(null, 4);

            // TODO replace this with dispenser materialdata class (as soon as it provides access to
            // this property)
            data.setData((byte) (data.getData() | 0x8));
            state.update();
        } else if (!powered && isTriggered) {
            data.setData((byte) (data.getData() & ~0x8));
            state.update();
        }
    }

    /**
     * Dispense an item from the given block if it's a dispenser.
     *
     * @param block the dispenser block
     */
    public void trigger(GlowBlock block) {
        BlockEntity te = block.getBlockEntity();
        if (!(te instanceof DispenserEntity)) {
            return;
        }
        DispenserEntity teDispenser = (DispenserEntity) te;

        GlowDispenser dispenser = (GlowDispenser) teDispenser.getState();
        dispenser.dispense();
    }
}
