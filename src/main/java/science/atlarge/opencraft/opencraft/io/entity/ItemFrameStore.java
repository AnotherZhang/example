package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.GlowHangingEntity.HangingFace;
import science.atlarge.opencraft.opencraft.entity.objects.GlowItemFrame;
import science.atlarge.opencraft.opencraft.io.nbt.NbtSerialization;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

class ItemFrameStore extends HangingStore<GlowItemFrame> {

    public ItemFrameStore() {
        super(GlowItemFrame.class, EntityType.ITEM_FRAME);
    }

    @Override
    public GlowItemFrame createEntity(Location location, CompoundTag compound) {
        // item frame will be set by loading code below
        return new GlowItemFrame(null, location, null);
    }

    @Override
    public void load(GlowItemFrame entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readItem("Item", entity::setItem);
        tag.readInt("Rotation", rotation -> entity.setRotation(Rotation.values()[rotation]));
    }

    @Override
    public void save(GlowItemFrame entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putByte("Facing", HangingFace.getByBlockFace(entity.getFacing()).ordinal());
        tag.putCompound("Item", NbtSerialization.writeItem(entity.getItem(), -1));
        tag.putInt("Rotation", entity.getRotation().ordinal());
    }
}
