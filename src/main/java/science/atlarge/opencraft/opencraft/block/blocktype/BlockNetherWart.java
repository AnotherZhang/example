package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.EventFactory;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;

import java.util.concurrent.ThreadLocalRandom;

public class BlockNetherWart extends BlockNeedsAttached {

    public BlockNetherWart() {
        setDrops(new ItemStack(Material.NETHER_STALK, 1));
    }

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        return block.getWorld().getBlockTypeIdAt(block.getX(), block.getY() - 1, block.getZ())
                == Material.SOUL_SAND.getId();
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        int cropState = block.getData();
        if (cropState < NetherWartsState.RIPE.ordinal()
                && ThreadLocalRandom.current().nextInt(10) == 0) {
            cropState++;
            GlowBlockState state = block.getState();
            state.setRawData((byte) cropState);
            BlockGrowEvent growEvent = new BlockGrowEvent(block, state);
            EventFactory.getInstance().callEvent(growEvent);
            if (!growEvent.isCancelled()) {
                state.update(true);
            }
        }
    }
}
