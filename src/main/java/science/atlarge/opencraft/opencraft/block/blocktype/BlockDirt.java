package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.block.GlowBlock;

import java.util.Arrays;
import java.util.Collection;

public class BlockDirt extends BlockType {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        //TODO switch to MaterialData instead of using magic values
        if (block.getData() == 1) {
            //Coarse dirt
            return Arrays.asList(new ItemStack(Material.DIRT, 1, (short) 1));
        } else {
            //normal dirt and podzol drop normal dirt
            return Arrays.asList(new ItemStack(Material.DIRT));
        }
    }
}
