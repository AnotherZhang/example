package science.atlarge.opencraft.opencraft.block.itemtype;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import science.atlarge.opencraft.opencraft.EventFactory;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;

public class ItemMilk extends ItemFood {

    public ItemMilk() {
        super(0, 0);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        PlayerItemConsumeEvent event1 = new PlayerItemConsumeEvent(player, item);
        EventFactory.getInstance().callEvent(event1);
        if (event1.isCancelled()) {
            return false;
        }

        player.setUsageItem(null);
        player.setUsageTime(0);

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        player.getInventory().getItemInHand().setType(Material.BUCKET);

        return true;
    }
}
