package science.atlarge.opencraft.opencraft.dispenser;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.blocktype.BlockDispenser;
import science.atlarge.opencraft.opencraft.inventory.GlowInventory;

import java.util.ArrayList;
import java.util.List;

public class ArmorDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    protected ItemStack dispenseStack(GlowBlock block, ItemStack stack) {
        BlockFace facing = BlockDispenser.getFacing(block);
        Location targetLocation = block.getLocation().clone()
                .add(facing.getModX(), facing.getModY(), facing.getModZ());

        // Find all nearby entities and see if they are players or armor stands
        List<LivingEntity> entities = new ArrayList<>();
        for (Entity entity : targetLocation.getWorld().getNearbyEntities(targetLocation, 3, 3, 3)) {
            switch (entity.getType()) {
                case PLAYER:
                case ARMOR_STAND:
                    entities.add((LivingEntity) entity);
                    break;
                default:
                    // do nothing
            }
        }

        boolean targetLocationTest1;
        boolean targetLocationTest2;
        // Loop through entities to see if any are in the location where armor would be dispensed
        for (LivingEntity player : entities) {
            targetLocationTest1 = (player.getLocation().getBlockX() == targetLocation.getX()
                    && player.getLocation().getBlockY() == targetLocation.getY()
                    && player.getLocation().getBlockZ() == targetLocation.getZ());
            targetLocationTest2 = (player.getEyeLocation().getBlockX() == targetLocation.getX()
                    && player.getEyeLocation().getBlockY() == targetLocation.getY()
                    && player.getEyeLocation().getBlockZ() == targetLocation.getZ());
            if ((targetLocationTest1 || targetLocationTest2)) {
                return ((GlowInventory) ((Player) player).getInventory())
                        .tryToFillSlots(stack, 36, 40);
            }
        }
        return INSTANCE.dispense(block, stack); // Fallback
    }
}
