package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;

public class BlockCarpet extends BlockNeedsAttached {

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        return block.getWorld().getBlockTypeIdAt(block.getX(), block.getY() - 1, block.getZ())
                != Material.AIR.getId();
    }
}
