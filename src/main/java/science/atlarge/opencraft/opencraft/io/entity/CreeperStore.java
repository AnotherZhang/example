package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.monster.GlowCreeper;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

class CreeperStore extends MonsterStore<GlowCreeper> {

    public CreeperStore() {
        super(GlowCreeper.class, EntityType.CREEPER, GlowCreeper::new);
    }

    @Override
    public void load(GlowCreeper entity, CompoundTag compound) {
        super.load(entity, compound);
        compound.readBoolean("powered", entity::setPowered);
        entity.setExplosionRadius(compound.tryGetInt("ExplosionRadius").orElse(3));
        entity.setMaxFuseTicks(compound.tryGetInt("Fuse").orElse(30));
        entity.setIgnited(compound.getBoolean("ignited", false));
    }

    @Override
    public void save(GlowCreeper entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("powered", entity.isPowered());
        tag.putInt("ExplosionRadius", entity.getExplosionRadius());
        tag.putInt("Fuse", entity.getMaxFuseTicks());
        tag.putBool("ignited", entity.isIgnited());
    }

}
