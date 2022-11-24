package science.atlarge.opencraft.opencraft.block.itemtype;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;

public class ItemSnowball extends ItemProjectile {
    public ItemSnowball() {
        super(EntityType.SNOWBALL);
    }

    @Override
    public Projectile use(GlowPlayer player, ItemStack holding) {
        return player.launchProjectile(Snowball.class);
    }
}
