package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.inventory.GlowAnvilInventory;
import science.atlarge.opencraft.opencraft.inventory.ToolType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockAnvil extends BlockFalling {

    public BlockAnvil() {
        super(Material.ANVIL);
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
            Vector clickedLoc) {
        return player.openInventory(new GlowAnvilInventory(player)) != null;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        // This is replicated from BlockNeedsTool and has been copy/pasted because classes cannot
        // extend 2 parents
        ToolType neededTool = ToolType.PICKAXE;
        if (tool == null || !neededTool.matches(tool.getType())) {
            return Collections.emptyList();
        }

        return getMinedDrops(block);
    }

    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        ItemStack drop = new ItemStack(Material.ANVIL, 1, (short) (block.getData() / 4));
        return Arrays.asList(drop);
    }
}
