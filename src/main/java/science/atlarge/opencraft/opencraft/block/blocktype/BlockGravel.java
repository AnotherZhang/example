package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.block.GlowBlock;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class BlockGravel extends BlockFalling {

    public BlockGravel() {
        super(Material.GRAVEL);
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Collections.unmodifiableList(Arrays.asList(new ItemStack(
                ThreadLocalRandom.current().nextInt(10) == 1 ? Material.FLINT : Material.GRAVEL, 1)));
    }

}
