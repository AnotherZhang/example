package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;

public class BlockHay extends BlockType {

    public BlockHay() {
        setDrops(new ItemStack(Material.HAY_BLOCK));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
            ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        switch (face) {
            case NORTH:
            case SOUTH:
                state.setRawData((byte) 8);
                break;
            case WEST:
            case EAST:
                state.setRawData((byte) 4);
                break;
            case UP:
            case DOWN:
                state.setRawData((byte) 0);
                break;
            default:
                // do nothing
                // TODO: should this raise a warning?
        }
    }
}
