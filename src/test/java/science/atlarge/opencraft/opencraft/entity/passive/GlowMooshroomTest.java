package science.atlarge.opencraft.opencraft.entity.passive;

import org.bukkit.Material;
import org.junit.Test;
import science.atlarge.opencraft.opencraft.entity.GlowAnimalTest;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;

public class GlowMooshroomTest extends GlowAnimalTest<GlowMooshroom> {
    public GlowMooshroomTest() {
        super(GlowMooshroom::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.WHEAT), entity.getBreedingFoods());
    }
}
