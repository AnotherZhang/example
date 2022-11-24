package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.block.GlowBlock;

import java.util.Collection;
import java.util.Collections;

public class BlockDropless extends BlockType {

    @Override
    public final Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Collections.emptyList();
    }
}
