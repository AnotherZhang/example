package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;
import science.atlarge.opencraft.opencraft.EventFactory;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.inventory.GlowItemFactory;
import science.atlarge.opencraft.opencraft.inventory.MaterialMatcher;
import science.atlarge.opencraft.opencraft.inventory.ToolType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BlockCauldron extends BlockNeedsTool {

    private static final byte LEVEL_EMPTY = 0;
    private static final byte LEVEL_FULL = 3;

    private static final Collection<ItemStack> DROP = Collections
            .singletonList(new ItemStack(Material.CAULDRON_ITEM));

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
            Vector clickedLoc) {
        if (player.getItemInHand() == null) {
            return super.blockInteract(player, block, face, clickedLoc);
        }

        switch (player.getItemInHand().getType()) {
            // todo: filling the cauldron by emptying a bottle
            // todo: emptying the cauldron by filling a bucket
            case WATER_BUCKET:
                fillCauldron(player, block);
                return true;

            case GLASS_BOTTLE:
                fillBottle(player, block);
                return true;

            case LEATHER_BOOTS:
            case LEATHER_LEGGINGS:
            case LEATHER_CHESTPLATE:
            case LEATHER_HELMET:
                return bleachLeatherArmor(player, block);

            case BANNER:
                return bleachBanner(player, block);

            default:
                return super.blockInteract(player, block, face, clickedLoc);
        }
    }

    private void fillCauldron(GlowPlayer player, GlowBlock block) {
        // fired when a player fills the cauldron using a water bucket
        if (block.getData() < LEVEL_FULL) {
            if (!setCauldronLevel(block, LEVEL_FULL, player,
                    CauldronLevelChangeEvent.ChangeReason.BUCKET_EMPTY)) {
                return;
            }
            if (player.getGameMode() != GameMode.CREATIVE) {
                player.getItemInHand().setType(Material.BUCKET);
            }
        }
    }

    private void fillBottle(GlowPlayer player, GlowBlock block) {
        // fired when a player fills an empty bottle from the cauldron
        if (block.getData() > LEVEL_EMPTY) {
            if (!setCauldronLevel(block, block.getData() - 1, player,
                    CauldronLevelChangeEvent.ChangeReason.BOTTLE_FILL)) {
                return;
            }
            if (player.getGameMode() != GameMode.CREATIVE) {
                Map<Integer, ItemStack> drops = player.getInventory()
                        .addItem(new ItemStack(Material.POTION));
                if (!drops.isEmpty()) {
                    player.getWorld()
                            .dropItemNaturally(player.getLocation(), new ItemStack(Material.POTION));
                }

                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            }
        }
    }

    private boolean bleachBanner(GlowPlayer player, GlowBlock block) {
        // fired when a player bleaches a banner using the cauldron
        if (player.getGameMode() == GameMode.CREATIVE) {
            return false;
        }

        if (block.getData() > LEVEL_EMPTY) {
            ItemStack inHand = player.getItemInHand();
            BannerMeta meta = (BannerMeta) inHand.getItemMeta();
            List<Pattern> layers = meta.getPatterns();
            if (layers == null || layers.isEmpty()) {
                return false;
            }
            if (!setCauldronLevel(block, block.getData() - 1, player,
                    CauldronLevelChangeEvent.ChangeReason.BANNER_WASH)) {
                return false;
            }
            meta.setPatterns(layers);
            inHand.setItemMeta(meta);
            return true;
        } else {
            return false;
        }
    }

    private boolean bleachLeatherArmor(GlowPlayer player, GlowBlock block) {
        // fired when a player bleaches a leather armor piece using the cauldron
        if (block.getData() > LEVEL_EMPTY) {
            if (!setCauldronLevel(block, block.getData() - 1, player,
                    CauldronLevelChangeEvent.ChangeReason.ARMOR_WASH)) {
                return false;
            }
            ItemStack inHand = player.getItemInHand();
            LeatherArmorMeta im = (LeatherArmorMeta) inHand.getItemMeta();
            im.setColor(GlowItemFactory.instance().getDefaultLeatherColor());
            inHand.setItemMeta(im);
            return true;
        } else {
            return false;
        }
    }

    private boolean setCauldronLevel(GlowBlock block, int newLevel, Entity entity,
            CauldronLevelChangeEvent.ChangeReason reason) {
        int oldLevel = (int) block.getData();
        CauldronLevelChangeEvent event = EventFactory.getInstance().callEvent(
                new CauldronLevelChangeEvent(block, entity, reason, oldLevel, newLevel));
        if (!event.isCancelled()) {
            block.setData((byte) event.getNewLevel());
        }
        return !event.isCancelled();
    }

    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        return DROP;
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }
}
