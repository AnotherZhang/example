package science.atlarge.opencraft.opencraft.entity.projectile;

import org.bukkit.Location;
import science.atlarge.opencraft.opencraft.entity.GlowEntityTest;

import java.util.function.Function;

public abstract class GlowProjectileTest<T extends GlowProjectile> extends GlowEntityTest<T> {

    protected GlowProjectileTest(
            Function<? super Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
