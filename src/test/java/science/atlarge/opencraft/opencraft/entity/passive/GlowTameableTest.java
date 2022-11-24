package science.atlarge.opencraft.opencraft.entity.passive;

import org.bukkit.Location;
import science.atlarge.opencraft.opencraft.entity.GlowAnimalTest;

import java.util.function.Function;

public abstract class GlowTameableTest<T extends GlowTameable> extends GlowAnimalTest<T> {
    protected GlowTameableTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
