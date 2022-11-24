package science.atlarge.opencraft.opencraft.entity.projectile;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.WitherSkull;
import science.atlarge.opencraft.opencraft.net.message.play.entity.SpawnObjectMessage;

public class GlowWitherSkull extends GlowFireball implements WitherSkull {
    @Getter
    @Setter
    private boolean charged;

    public GlowWitherSkull(Location location) {
        super(location);
    }

    @Override
    protected int getObjectId() {
        return SpawnObjectMessage.WITHER_SKULL;
    }
}
