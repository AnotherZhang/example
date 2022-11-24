package science.atlarge.opencraft.opencraft.block.entity;

import org.bukkit.event.inventory.InventoryType;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.block.entity.state.GlowDispenser;
import science.atlarge.opencraft.opencraft.inventory.GlowInventory;

public class DispenserEntity extends ContainerEntity {

    public DispenserEntity(GlowBlock block) {
        super(block, new GlowInventory(new GlowDispenser(block), InventoryType.DISPENSER));
        setOwnSaveId();
    }

    protected void setOwnSaveId() {
        setSaveId("minecraft:dispenser");
    }

    @Override
    public GlowBlockState getState() {
        return new GlowDispenser(block);
    }
}
