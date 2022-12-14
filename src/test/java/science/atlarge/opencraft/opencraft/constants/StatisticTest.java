package science.atlarge.opencraft.opencraft.constants;

import org.bukkit.Statistic;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link GlowStatistic}.
 */
public class StatisticTest {

    @EnumSource(Statistic.class)
    @ParameterizedTest
    public void testNameAvailable(Statistic statistic) {
        if (statistic.getType() != Statistic.Type.UNTYPED) {
            // typed statistics not yet tested
            return;
        }
        assertThat("Name missing for untyped statistic " + statistic,
            GlowStatistic.getName(statistic), not(sameInstance(null)));
    }

}
