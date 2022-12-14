package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.GlowTntPrimed;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

class TntPrimedStorage extends EntityStore<GlowTntPrimed> {

    public TntPrimedStorage() {
        super(GlowTntPrimed.class, EntityType.PRIMED_TNT);
    }

    @Override
    public GlowTntPrimed createEntity(Location location, CompoundTag compound) {
        return new GlowTntPrimed(location, null);
    }

    @Override
    public void load(GlowTntPrimed entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readByte("Fuse", entity::setFuseTicks);
    }

    @Override
    public void save(GlowTntPrimed entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putByte("Fuse", entity.getFuseTicks());
    }
}
