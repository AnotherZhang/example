package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.monster.GlowElderGuardian;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

class ElderGuardianStore extends MonsterStore<GlowElderGuardian> {

    public ElderGuardianStore() {
        super(GlowElderGuardian.class, EntityType.ELDER_GUARDIAN, GlowElderGuardian::new);
    }

    @Override
    public void load(GlowElderGuardian entity, CompoundTag compound) {
        super.load(entity, compound);
    }

    @Override
    public void save(GlowElderGuardian entity, CompoundTag compound) {
        super.save(entity, compound);
    }

}
