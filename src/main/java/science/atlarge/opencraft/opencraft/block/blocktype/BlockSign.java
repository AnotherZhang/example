package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;
import org.bukkit.util.Vector;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.block.ItemTable;
import science.atlarge.opencraft.opencraft.block.entity.BlockEntity;
import science.atlarge.opencraft.opencraft.block.entity.SignEntity;
import science.atlarge.opencraft.opencraft.chunk.GlowChunk;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;

public class BlockSign extends BlockNeedsAttached {

    public BlockSign() {
        setDrops(new ItemStack(Material.SIGN));
    }

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new SignEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
            ItemStack holding, Vector clickedLoc) {
        state.setType(getMaterial());
        if (!(state.getData() instanceof Sign)) {
            warnMaterialData(Sign.class, state.getData());
            return;
        }
        Sign sign = (Sign) state.getData();
        sign.setFacingDirection(sign.isWallSign() ? face : player.getFacing().getOppositeFace());
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
            GlowBlockState oldState) {
        player.openSignEditor(block.getLocation());
    }

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        Material targetMat = ItemTable.instance().getBlock(
                block.getRelative(against.getOppositeFace()).getType()).getMaterial();
        return canAttachTo(block, against) || targetMat == Material.SIGN_POST
                || targetMat == Material.WALL_SIGN;
    }
}
