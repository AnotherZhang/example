package science.atlarge.opencraft.opencraft.dispenser;

import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.block.GlowBlock;

public interface DispenseBehavior {

    ItemStack dispense(GlowBlock block, ItemStack stack);
}
