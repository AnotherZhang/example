package science.atlarge.opencraft.opencraft.entity.passive;

import org.bukkit.Material;
import org.junit.Test;
import science.atlarge.opencraft.opencraft.entity.GlowAnimalTest;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;

public class GlowChickenTest extends GlowAnimalTest<GlowChicken> {

    public GlowChickenTest() {
        super(GlowChicken::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.SEEDS, Material.PUMPKIN_SEEDS, Material.MELON_SEEDS, Material.BEETROOT_SEEDS),
                entity.getBreedingFoods());
    }
}
