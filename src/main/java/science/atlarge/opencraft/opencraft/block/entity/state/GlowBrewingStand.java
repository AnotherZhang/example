package science.atlarge.opencraft.opencraft.block.entity.state;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.entity.BrewingStandEntity;

public class GlowBrewingStand extends GlowContainer implements BrewingStand {

    @Getter
    @Setter
    private int brewingTime;
    @Getter
    @Setter
    private int fuelLevel;

    public GlowBrewingStand(GlowBlock block) {
        super(block);
        brewingTime = getBlockEntity().getBrewTime();
    }

    public GlowBrewingStand(GlowBlock block, int brewTime) {
        super(block);
        this.brewingTime = brewTime;
    }

    private BrewingStandEntity getBlockEntity() {
        return (BrewingStandEntity) getBlock().getBlockEntity();
    }

    @Override
    public BrewerInventory getInventory() {
        return (BrewerInventory) getBlockEntity().getInventory();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            BrewingStandEntity stand = getBlockEntity();
            stand.setBrewTime(brewingTime);
            stand.updateInRange();
        }
        return result;
    }

    @Override
    public BrewerInventory getSnapshotInventory() {
        throw new UnsupportedOperationException();
    }
}
