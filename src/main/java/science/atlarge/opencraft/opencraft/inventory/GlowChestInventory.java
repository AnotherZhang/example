package science.atlarge.opencraft.opencraft.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import science.atlarge.opencraft.opencraft.block.entity.state.GlowChest;

/**
 * A class which represents an chest inventory.
 */
public class GlowChestInventory extends GlowInventory {

    private GlowChest chest;

    public GlowChestInventory(GlowChest chest) {
        super(chest, InventoryType.CHEST);
        this.chest = chest;
    }

    @Override
    public void addViewer(HumanEntity viewer) {
        super.addViewer(viewer);
        chest.getBlockEntity().addViewer();
    }

    @Override
    public void removeViewer(HumanEntity viewer) {
        super.removeViewer(viewer);
        chest.getBlockEntity().removeViewer();
    }
}
