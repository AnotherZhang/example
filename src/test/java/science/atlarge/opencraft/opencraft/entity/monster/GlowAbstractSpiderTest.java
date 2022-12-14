package science.atlarge.opencraft.opencraft.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.Spider;

import java.util.function.Function;

/**
 * Necessary because GlowCaveSpider doesn't extend GlowSpider, but both have similar expected
 * behavior.
 */
public abstract class GlowAbstractSpiderTest<T extends GlowMonster & Spider>
        extends GlowMonsterTest<T> {
    protected GlowAbstractSpiderTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
