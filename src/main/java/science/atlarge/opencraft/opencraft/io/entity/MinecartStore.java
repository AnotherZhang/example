package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import science.atlarge.opencraft.opencraft.entity.objects.GlowMinecart;
import science.atlarge.opencraft.opencraft.io.nbt.NbtSerialization;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

import java.util.function.Function;

public class MinecartStore extends EntityStore<GlowMinecart> {

    private GlowMinecart.MinecartType minecartType;

    public MinecartStore(GlowMinecart.MinecartType minecartType) {
        super(minecartType.getMinecartClass(), minecartType.getEntityType());
        this.minecartType = minecartType;
    }

    @Override
    public GlowMinecart createEntity(Location location, CompoundTag compound) {
        Function<? super Location, ? extends GlowMinecart> creator = minecartType.getCreator();
        return creator == null ? null : creator.apply(location);
    }

    @Override
    public void load(GlowMinecart entity, CompoundTag tag) {
        super.load(entity, tag);
        if (entity instanceof InventoryHolder) {
            Inventory inventory = ((InventoryHolder) entity).getInventory();
            if (inventory != null) {
                tag.readCompoundList("Items",
                        items -> inventory.setContents(NbtSerialization.readInventory(
                                items, 0, inventory.getSize())));
            }
        }
        // todo
    }

    @Override
    public void save(GlowMinecart entity, CompoundTag tag) {
        super.save(entity, tag);
        if (entity instanceof InventoryHolder) {
            InventoryHolder inv = (InventoryHolder) entity;
            if (inv.getInventory() != null) {
                tag.putCompoundList("Items",
                        NbtSerialization.writeInventory(inv.getInventory().getContents(), 0));
            }
        }
        // todo
    }
}
