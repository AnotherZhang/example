package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.objects.GlowItem;
import science.atlarge.opencraft.opencraft.io.nbt.NbtSerialization;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

class ItemStore extends EntityStore<GlowItem> {

    public ItemStore() {
        super(GlowItem.class, EntityType.DROPPED_ITEM);
    }

    @Override
    public GlowItem createEntity(Location location, CompoundTag compound) {
        // item will be set by loading code below
        return new GlowItem(location, null);
    }

    // todo: the following tags
    // - "Health"
    // - "Owner"
    // - "Thrower"

    @Override
    public void load(GlowItem entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readItem("Item", entity::setItemStack);
        tag.readShort("Age", entity::setTicksLived);
        tag.readShort("PickupDelay", entity::setPickupDelay);
    }

    @Override
    public void save(GlowItem entity, CompoundTag tag) {
        super.save(entity, tag);

        tag.putCompound("Item", NbtSerialization.writeItem(entity.getItemStack(), -1));
        tag.putShort("Age", entity.getTicksLived());
        tag.putShort("Health", 5);
        tag.putShort("PickupDelay", entity.getPickupDelay());
    }
}
