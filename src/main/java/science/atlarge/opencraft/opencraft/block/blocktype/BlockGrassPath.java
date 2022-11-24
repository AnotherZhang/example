package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import science.atlarge.opencraft.opencraft.block.GlowBlock;

public class BlockGrassPath extends BlockDirectDrops {

    public BlockGrassPath() {
        super(Material.DIRT);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
            Material oldType, byte oldData, Material newType, byte newData) {
        if (face == BlockFace.UP && newType.isSolid()) {
            block.setType(Material.DIRT);
        }
    }
}
