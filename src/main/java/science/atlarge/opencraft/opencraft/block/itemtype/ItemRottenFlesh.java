package science.atlarge.opencraft.opencraft.block.itemtype;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.util.TickUtil;

public class ItemRottenFlesh extends ItemFood {

    public ItemRottenFlesh() {
        super(4, 0.8f);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!super.eat(player, item)) {
            return false;
        }

        if (Math.random() < 0.8) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,
                    TickUtil.secondsToTicks(30), 0), true);
        }
        return true;
    }
}
