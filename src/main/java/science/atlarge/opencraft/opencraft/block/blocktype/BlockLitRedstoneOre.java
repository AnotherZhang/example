package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.event.block.BlockFadeEvent;
import science.atlarge.opencraft.opencraft.EventFactory;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;

public class BlockLitRedstoneOre extends BlockRedstoneOre {

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        GlowBlockState state = block.getState();
        state.setType(Material.REDSTONE_ORE);
        BlockFadeEvent fadeEvent = new BlockFadeEvent(block, state);
        EventFactory.getInstance().callEvent(fadeEvent);
        if (!fadeEvent.isCancelled()) {
            state.update(true);
        }
    }
}
