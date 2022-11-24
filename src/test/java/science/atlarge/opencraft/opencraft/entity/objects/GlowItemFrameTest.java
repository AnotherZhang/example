package science.atlarge.opencraft.opencraft.entity.objects;

import org.bukkit.block.BlockFace;
import science.atlarge.opencraft.opencraft.entity.GlowHangingEntityTest;

public class GlowItemFrameTest extends GlowHangingEntityTest<GlowItemFrame> {
    public GlowItemFrameTest() {
        super(location -> new GlowItemFrame(null, location, BlockFace.SOUTH));
    }
}
