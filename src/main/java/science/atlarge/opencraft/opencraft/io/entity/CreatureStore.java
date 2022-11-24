package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.GlowCreature;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

abstract class CreatureStore<T extends GlowCreature> extends LivingEntityStore<T> {

    public CreatureStore(Class<T> clazz, EntityType type) {
        super(clazz, type);
    }

    public CreatureStore(Class<T> clazz, String type) {
        super(clazz, type);
    }

    @Override
    public void load(T entity, CompoundTag compound) {
        super.load(entity, compound);
    }

    @Override
    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
    }
}
