package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.passive.GlowWolf;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

class WolfStore extends TameableStore<GlowWolf> {

    public WolfStore() {
        super(GlowWolf.class, EntityType.WOLF, GlowWolf::new);
    }

    @Override
    public void load(GlowWolf entity, CompoundTag compound) {
        super.load(entity, compound);
        compound.readByte("CollarColor",
                color -> entity.setCollarColor(DyeColor.getByDyeData(color)));
        compound.readBoolean("Angry", entity::setAngry);
    }

    @Override
    public void save(GlowWolf entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putByte("CollarColor", entity.getCollarColor().getDyeData());
        tag.putBool("Angry", entity.isAngry());
    }

}
