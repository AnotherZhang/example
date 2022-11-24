package science.atlarge.opencraft.opencraft.entity.objects;

import org.bukkit.Material;
import science.atlarge.opencraft.opencraft.entity.GlowEntityTest;

public class GlowFallingBlockTest extends GlowEntityTest<GlowFallingBlock> {
    public GlowFallingBlockTest() {
        super(location -> new GlowFallingBlock(location, Material.GRAVEL, (byte) 0));
    }
}
