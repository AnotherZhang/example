package science.atlarge.opencraft.opencraft.entity.monster;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import science.atlarge.opencraft.opencraft.entity.meta.MetadataIndex;

public class GlowGhast extends GlowMonster implements Ghast {

    @Getter
    @Setter
    private int explosionPower;

    public GlowGhast(Location loc) {
        super(loc, EntityType.GHAST, 10);
        setBoundingBox(4, 4);
        setGravity(false);
        setAirDragMultiplier(0.2);
    }

    public boolean isAttacking() {
        return metadata.getBoolean(MetadataIndex.GHAST_ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        metadata.set(MetadataIndex.GHAST_ATTACKING, attacking);
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_GHAST_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_GHAST_HURT;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_GHAST_AMBIENT;
    }
}
