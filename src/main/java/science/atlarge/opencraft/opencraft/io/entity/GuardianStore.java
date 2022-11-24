package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.monster.GlowGuardian;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

class GuardianStore extends MonsterStore<GlowGuardian> {

    public GuardianStore() {
        super(GlowGuardian.class, EntityType.GUARDIAN, GlowGuardian::new);
    }

    @Override
    public void load(GlowGuardian entity, CompoundTag compound) {
        super.load(entity, compound);
    }

    @Override
    public void save(GlowGuardian entity, CompoundTag compound) {
        super.save(entity, compound);
    }

}
