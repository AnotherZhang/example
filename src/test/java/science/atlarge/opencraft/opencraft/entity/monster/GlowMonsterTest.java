package science.atlarge.opencraft.opencraft.entity.monster;

import org.bukkit.Location;
import science.atlarge.opencraft.opencraft.entity.GlowCreatureTest;

import java.util.function.Function;

public abstract class GlowMonsterTest<T extends GlowMonster> extends GlowCreatureTest<T> {

    protected GlowMonsterTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
