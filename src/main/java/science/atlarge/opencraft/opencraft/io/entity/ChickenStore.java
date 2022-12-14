package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.passive.GlowChicken;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

class ChickenStore extends AgeableStore<GlowChicken> {

    public ChickenStore() {
        super(GlowChicken.class, EntityType.CHICKEN, GlowChicken::new);
    }

    @Override
    public void load(GlowChicken entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setChickenJockey(compound.getBoolean("isChickenJockey", false));
        entity.setEggLayTime(compound.tryGetInt("EggLayTime").orElse(6000));

    }

    @Override
    public void save(GlowChicken entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("isChickenJockey", entity.isChickenJockey());
        tag.putInt("EggLayTime", entity.getEggLayTime());
    }

}
