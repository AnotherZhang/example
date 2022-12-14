package science.atlarge.opencraft.opencraft.block.itemtype;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.util.TickUtil;

public class ItemSpiderEye extends ItemFood {

    public ItemSpiderEye() {
        super(2, 3.2f);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!super.eat(player, item)) {
            return false;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON,
                TickUtil.secondsToTicks(5), 0), true);
        return true;
    }
}
