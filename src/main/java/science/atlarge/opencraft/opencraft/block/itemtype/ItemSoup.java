package science.atlarge.opencraft.opencraft.block.itemtype;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;

public class ItemSoup extends ItemFood {

    public ItemSoup(int foodLevel, float saturation) {
        super(foodLevel, saturation);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!handleEat(player, item)) {
            return false;
        }

        item.setType(Material.BOWL);
        return true;
    }
}
