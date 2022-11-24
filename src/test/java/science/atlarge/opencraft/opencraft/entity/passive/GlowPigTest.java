package science.atlarge.opencraft.opencraft.entity.passive;

import org.bukkit.Material;
import org.junit.Test;
import science.atlarge.opencraft.opencraft.entity.GlowAnimalTest;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;

public class GlowPigTest extends GlowAnimalTest<GlowPig> {
    public GlowPigTest() {
        super(GlowPig::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.POTATO_ITEM, Material.CARROT_ITEM, Material.BEETROOT),
                entity.getBreedingFoods());
    }
}
