package science.atlarge.opencraft.opencraft.entity.objects;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import science.atlarge.opencraft.opencraft.entity.GlowEntityTest;

@RunWith(Parameterized.class)
public class GlowMinecartTest extends GlowEntityTest<GlowMinecart> {

    @Parameterized.Parameters(name = "{0}")
    public static GlowMinecart.MinecartType[] data() {
        return GlowMinecart.MinecartType.values();
    }

    public GlowMinecartTest(GlowMinecart.MinecartType type) {
        super(type.getCreator());
    }
}
