package science.atlarge.opencraft.opencraft.block.entity;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import science.atlarge.opencraft.opencraft.EventFactory;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.ServerProvider;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.block.entity.state.GlowFurnace;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.inventory.GlowFurnaceInventory;
import science.atlarge.opencraft.opencraft.inventory.crafting.CraftingManager;
import science.atlarge.opencraft.opencraft.util.InventoryUtil;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

public class FurnaceEntity extends ContainerEntity {

    @Getter
    @Setter
    private short burnTime;
    @Getter
    @Setter
    private short cookTime;
    private short burnTimeFuel;

    public FurnaceEntity(GlowBlock block) {
        super(block, new GlowFurnaceInventory(new GlowFurnace(block, (short) 0, (short) 0)));
        setSaveId("minecraft:furnace");
    }

    @Override
    public GlowBlockState getState() {
        return new GlowFurnace(block);
    }

    @Override
    public void update(GlowPlayer player) {
        super.update(player);
        GlowWorld world = player.getWorld();
        // TODO: it is possible that this causes a broadcast message to be sent multiple times.
        world.sendBlockChange(getBlock().getLocation(),
                getBurnTime() > 0 ? Material.BURNING_FURNACE : Material.FURNACE, getBlock().getData());
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putShort("BurnTime", burnTime);
        tag.putShort("CookTime", cookTime);
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        tag.readShort("BurnTime", this::setBurnTime);
        tag.readShort("CookTime", this::setCookTime);
    }

    /**
     * Advances the cooking process for the tick.
     */
    // TODO: Change block on burning
    public void burn() {
        GlowFurnaceInventory inv = (GlowFurnaceInventory) getInventory();
        boolean sendChange = false;
        if (burnTime > 0) {
            burnTime--;
            sendChange = true;
        }
        boolean isBurnable = isBurnable();
        if (cookTime > 0 && isBurnable) {
            cookTime++;
            sendChange = true;
        } else if (burnTime != 0) {
            cookTime = 0;
            sendChange = true;
        }

        if (cookTime == 0 && isBurnable) {
            cookTime = 1;
            sendChange = true;
        }

        if (burnTime == 0) {
            if (isBurnable) {
                CraftingManager cm = ((GlowServer) ServerProvider.getServer()).getCraftingManager();
                FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(block, inv.getFuel(),
                        cm.getFuelTime(inv.getFuel().getType()));
                EventFactory.getInstance().callEvent(burnEvent);
                if (!burnEvent.isCancelled() && burnEvent.isBurning()) {
                    burnTime = (short) burnEvent.getBurnTime();
                    burnTimeFuel = burnTime;
                    if (inv.getFuel().getAmount() == 1) {
                        if (inv.getFuel().getType().equals(Material.LAVA_BUCKET)) {
                            inv.setFuel(new ItemStack(Material.BUCKET));
                        } else {
                            inv.setFuel(null);
                        }
                    } else {
                        inv.getFuel().setAmount(inv.getFuel().getAmount() - 1);
                    }
                    sendChange = true;
                } else if (cookTime != 0) {
                    if (cookTime % 2 == 0) {
                        cookTime = (short) (cookTime - 2);
                    } else {
                        cookTime--;
                    }
                    sendChange = true;
                }
            } else if (cookTime != 0) {
                if (cookTime % 2 == 0) {
                    cookTime = (short) (cookTime - 2);
                } else {
                    cookTime--;
                }
                sendChange = true;
            }
        }

        if (cookTime == 200) {
            CraftingManager cm = ((GlowServer) ServerProvider.getServer()).getCraftingManager();
            Recipe recipe = cm.getFurnaceRecipe(inv.getSmelting());
            if (recipe != null) {
                FurnaceSmeltEvent smeltEvent = new FurnaceSmeltEvent(block, inv.getSmelting(),
                        recipe.getResult());
                EventFactory.getInstance().callEvent(smeltEvent);
                if (!smeltEvent.isCancelled()) {
                    if (inv.getSmelting().getType().equals(Material.SPONGE)
                            && inv.getSmelting().getData().getData() == 1 && inv.getFuel() != null
                            && inv.getFuel().getType().equals(Material.BUCKET)
                            && inv.getFuel().getAmount() == 1) {
                        inv.setFuel(new ItemStack(Material.WATER_BUCKET));
                    }
                    if (inv.getResult() == null || inv.getResult().getType().equals(Material.AIR)) {
                        inv.setResult(smeltEvent.getResult());
                    } else if (inv.getResult().getType().equals(smeltEvent.getResult().getType())) {
                        inv.getResult().setAmount(
                                inv.getResult().getAmount() + smeltEvent.getResult().getAmount());
                    }
                    if (inv.getSmelting().getAmount() == 1) {
                        inv.setSmelting(null);
                    } else {
                        inv.getSmelting().setAmount(inv.getSmelting().getAmount() - 1);
                    }
                }
                cookTime = 0;
                sendChange = true;
            }
        }
        inv.getViewersSet().forEach(human -> {
            human.setWindowProperty(Property.BURN_TIME, burnTime);
            human.setWindowProperty(Property.TICKS_FOR_CURRENT_FUEL, burnTimeFuel);
            human.setWindowProperty(Property.COOK_TIME, cookTime);
            human.setWindowProperty(Property.TICKS_FOR_CURRENT_SMELTING, 200);
        });
        if (!isBurnable && burnTime == 0 && cookTime == 0) {
            getState().getBlock().getWorld().cancelPulse(getState().getBlock());
            sendChange = true;
        }
        if (sendChange) {
            updateInRange();
        }
    }

    private boolean isBurnable() {
        GlowFurnaceInventory inv = (GlowFurnaceInventory) getInventory();
        if ((burnTime != 0 || !InventoryUtil.isEmpty(inv.getFuel())) && !InventoryUtil
                .isEmpty(inv.getSmelting())) {
            if ((InventoryUtil.isEmpty(inv.getFuel()) || InventoryUtil.isEmpty(inv.getSmelting()))
                    && burnTime == 0) {
                return false;
            }
            CraftingManager cm = ((GlowServer) ServerProvider.getServer()).getCraftingManager();
            if (burnTime != 0 || cm.isFuel(inv.getFuel().getType())) {
                Recipe recipe = cm.getFurnaceRecipe(inv.getSmelting());
                if (recipe != null && (InventoryUtil.isEmpty(inv.getResult())
                        || inv.getResult().getType().equals(recipe.getResult().getType())
                        && inv.getResult().getAmount() + recipe.getResult().getAmount() <= recipe
                        .getResult().getMaxStackSize())) {
                    return true;
                }
            }
        }
        return false;
    }
}
