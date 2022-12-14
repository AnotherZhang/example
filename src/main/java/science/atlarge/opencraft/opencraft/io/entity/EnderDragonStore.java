package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.monster.complex.GlowEnderDragon;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

public class EnderDragonStore extends EntityStore<GlowEnderDragon> {

    public EnderDragonStore() {
        super(GlowEnderDragon.class, EntityType.ENDER_DRAGON);
    }

    @Override
    public void load(GlowEnderDragon entity, CompoundTag tag) {
        super.load(entity, tag);
        entity.setPhase(
                tag.tryGetInt("DragonPhase")
                        .map(phase -> EnderDragon.Phase.values()[phase])
                        .orElse(EnderDragon.Phase.HOVER));
    }

    @Override
    public void save(GlowEnderDragon entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("DragonPhase", entity.getPhase().ordinal());
    }

    @Override
    public GlowEnderDragon createEntity(Location location, CompoundTag compound) {
        try {
            return GlowEnderDragon.class.getConstructor(Location.class).newInstance(location);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Not implemented yet.");
        }
    }
}
