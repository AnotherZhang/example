package science.atlarge.opencraft.opencraft.entity.objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.entity.GlowEntityTest;

public class GlowItemTest extends GlowEntityTest<GlowItem> {
    public GlowItemTest() {
        super(location -> new GlowItem(location, new ItemStack(Material.DIRT)));
    }
}
