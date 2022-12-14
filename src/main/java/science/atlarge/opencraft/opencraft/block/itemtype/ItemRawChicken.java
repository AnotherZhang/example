package science.atlarge.opencraft.opencraft.block.itemtype;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.util.TickUtil;

public class ItemRawChicken extends ItemFood {

    public ItemRawChicken() {
        super(2, 1.2f);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!super.eat(player, item)) {
            return false;
        }

        if (Math.random() < 0.3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,
                    TickUtil.secondsToTicks(30), 0), true);
        }
        return true;
    }
}
