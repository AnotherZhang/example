package science.atlarge.opencraft.opencraft.entity.passive;

import org.bukkit.Material;
import org.junit.Test;
import science.atlarge.opencraft.opencraft.entity.GlowAnimalTest;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;

public class GlowCowTest extends GlowAnimalTest<GlowCow> {

    public GlowCowTest() {
        super(GlowCow::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.WHEAT), entity.getBreedingFoods());
    }
}
