package science.atlarge.opencraft.opencraft.entity.projectile;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import science.atlarge.opencraft.opencraft.net.message.play.entity.SpawnObjectMessage;

public class GlowSpectralArrow extends GlowArrow implements SpectralArrow {

    @Getter
    @Setter
    private int glowingTicks;

    public GlowSpectralArrow(Location location) {
        super(location);
        glowingTicks = 200;
    }

    @Override
    public void collide(LivingEntity entity) {
        super.collide(entity);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, glowingTicks, 1));
    }

    @Override
    protected int getObjectId() {
        return SpawnObjectMessage.SPECTRAL_ARROW;
    }
}
