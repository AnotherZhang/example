package science.atlarge.opencraft.opencraft.block.entity.state;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.FlowerPot;
import org.bukkit.material.MaterialData;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.block.entity.FlowerPotEntity;

public class GlowFlowerPot extends GlowBlockState implements FlowerPot {

    @Getter
    @Setter
    private MaterialData contents;

    /**
     * Creates an instance for the given block.
     *
     * @param block the flowerpot block
     */
    public GlowFlowerPot(GlowBlock block) {
        super(block);
        // Pre-1.7 uses block data and post-1.7 uses NBT data for flower pot contents.
        if (getBlockEntity() != null) {
            contents = getBlockEntity().getContents();
        } else if (hasFlowerPotData()) {
            contents = ((org.bukkit.material.FlowerPot) getData()).getContents();
        }
    }

    private FlowerPotEntity getBlockEntity() {
        return (FlowerPotEntity) getBlock().getBlockEntity();
    }

    private boolean hasFlowerPotData() {
        return getData() instanceof org.bukkit.material.FlowerPot;
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        // Pre-1.7 uses block data.
        if (getBlockEntity() == null && hasFlowerPotData()) {
            ((org.bukkit.material.FlowerPot) getData()).setContents(contents);
        }

        boolean result = super.update(force, applyPhysics);
        // Post-1.7 uses NBT data.
        if (result && getBlockEntity() != null) {
            FlowerPotEntity pot = getBlockEntity();

            pot.setContents(contents);
            pot.updateInRange();
        }
        return result;
    }
}
