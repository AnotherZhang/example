package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;

public class BlockDaylightDetector extends BlockType {

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
            Vector clickedLoc) {
        if (block.getType() == Material.DAYLIGHT_DETECTOR) {
            block.setType(Material.DAYLIGHT_DETECTOR_INVERTED);
        } else {
            block.setType(Material.DAYLIGHT_DETECTOR);
        }
        return true;
    }
}
