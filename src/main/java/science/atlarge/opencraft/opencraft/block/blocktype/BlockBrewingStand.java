package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.entity.BlockEntity;
import science.atlarge.opencraft.opencraft.block.entity.BrewingStandEntity;
import science.atlarge.opencraft.opencraft.chunk.GlowChunk;
import science.atlarge.opencraft.opencraft.inventory.MaterialMatcher;
import science.atlarge.opencraft.opencraft.inventory.ToolType;

import java.util.Arrays;
import java.util.Collection;

public class BlockBrewingStand extends BlockContainer {

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new BrewingStandEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    protected Collection<ItemStack> getBlockDrops(GlowBlock block) {
        return Arrays.asList(new ItemStack(Material.BREWING_STAND_ITEM));
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }
}
