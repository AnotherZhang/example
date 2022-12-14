package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.block.GlowBlock;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class BlockCarrot extends BlockCrops {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (block.getData() >= CropState.RIPE.ordinal()) {
            return Collections.unmodifiableList(Arrays.asList(
                    new ItemStack(Material.CARROT_ITEM, ThreadLocalRandom.current().nextInt(4) + 1)));
        } else {
            return Collections
                    .unmodifiableList(Arrays.asList(new ItemStack(Material.CARROT_ITEM, 1)));
        }
    }
}
