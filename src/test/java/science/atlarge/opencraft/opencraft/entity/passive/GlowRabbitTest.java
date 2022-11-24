package science.atlarge.opencraft.opencraft.entity.passive;

import org.bukkit.Material;
import org.junit.Test;
import science.atlarge.opencraft.opencraft.entity.GlowAnimalTest;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;

public class GlowRabbitTest extends GlowAnimalTest<GlowRabbit> {
    public GlowRabbitTest() {
        super(GlowRabbit::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.YELLOW_FLOWER, Material.GOLDEN_CARROT, Material.CARROT_ITEM),
                entity.getBreedingFoods());
    }
}
