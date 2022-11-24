package science.atlarge.opencraft.opencraft.entity.objects;

import org.bukkit.block.BlockFace;
import science.atlarge.opencraft.opencraft.entity.GlowHangingEntityTest;

public class GlowLeashHitchTest extends GlowHangingEntityTest<GlowLeashHitch> {
    public GlowLeashHitchTest() {
        super(location -> new GlowLeashHitch(location, BlockFace.SOUTH));
    }
}
