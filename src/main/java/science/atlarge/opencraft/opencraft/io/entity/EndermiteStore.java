package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.monster.GlowEndermite;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

class EndermiteStore extends MonsterStore<GlowEndermite> {

    public EndermiteStore() {
        super(GlowEndermite.class, EntityType.ENDERMITE, GlowEndermite::new);
    }

    @Override
    public void load(GlowEndermite entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setTicksLived(compound.tryGetInt("Lifetime").orElse(1));
        entity.setPlayerSpawned(compound.getBoolean("PlayerSpawned", true));
    }

    @Override
    public void save(GlowEndermite entity, CompoundTag compound) {
        super.save(entity, compound);
        compound.putInt("Lifetime", entity.getTicksLived());
        compound.putBool("PlayerSpawned", entity.isPlayerSpawned());
    }

}
