package science.atlarge.opencraft.opencraft.block.itemtype;

import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;

public class ItemEgg extends ItemProjectile {
    public ItemEgg() {
        super(EntityType.EGG);
    }

    @Override
    public Projectile use(GlowPlayer player, ItemStack holding) {
        return player.launchProjectile(Egg.class);
    }
}
