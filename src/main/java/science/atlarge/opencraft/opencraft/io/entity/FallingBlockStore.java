package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.constants.ItemIds;
import science.atlarge.opencraft.opencraft.entity.objects.GlowFallingBlock;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

class FallingBlockStore extends EntityStore<GlowFallingBlock> {

    public FallingBlockStore() {
        super(GlowFallingBlock.class, EntityType.FALLING_BLOCK);
    }


    @Override
    public GlowFallingBlock createEntity(Location location, CompoundTag compound) {
        // Falling block will be set by loading code below
        return new GlowFallingBlock(location, null, (byte) 0);
    }

    @Override
    public void load(GlowFallingBlock entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readString("Block", name -> entity.setMaterial(ItemIds.getBlock(name)));
        tag.readByte("Data", entity::setBlockData);
        tag.readBoolean("HurtEntities", entity::setHurtEntities);
        tag.readBoolean("DropItem", entity::setDropItem);
        tag.readCompound("TileEntityData", entity::setBlockEntityCompoundTag);
    }

    @Override
    public void save(GlowFallingBlock entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putString("Block", ItemIds.getName(entity.getMaterial()));
        tag.putByte("Data", entity.getBlockData());
        tag.putBool("DropItem", entity.getDropItem());
        tag.putBool("HurtEntities", entity.canHurtEntities());
        if (entity.getBlockEntityCompoundTag() != null) {
            tag.putCompound("TileEntityData", entity.getBlockEntityCompoundTag());
        }
    }
}
