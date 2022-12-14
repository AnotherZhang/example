package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.passive.GlowPig;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

class PigStore extends AgeableStore<GlowPig> {

    public PigStore() {
        super(GlowPig.class, EntityType.PIG, GlowPig::new);
    }

    @Override
    public void load(GlowPig entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setSaddle(compound.getBoolean("Saddle", false));
    }

    @Override
    public void save(GlowPig entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("Saddle", entity.hasSaddle());
    }
}
