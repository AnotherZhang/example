package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.objects.GlowExperienceOrb;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

class ExperienceOrbStore extends EntityStore<GlowExperienceOrb> {

    public ExperienceOrbStore() {
        super(GlowExperienceOrb.class, EntityType.EXPERIENCE_ORB);
    }

    @Override
    public GlowExperienceOrb createEntity(Location location, CompoundTag compound) {
        return new GlowExperienceOrb(location);
    }

    @Override
    public void load(GlowExperienceOrb entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readShort("Age", entity::setTicksLived);
        tag.readShort("Value", entity::setExperience);
    }

    @Override
    public void save(GlowExperienceOrb entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putShort("Age", entity.getTicksLived());
        tag.putShort("Value", entity.getExperience());
    }
}
