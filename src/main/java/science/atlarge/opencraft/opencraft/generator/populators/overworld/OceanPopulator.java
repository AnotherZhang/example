package science.atlarge.opencraft.opencraft.generator.populators.overworld;

import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class OceanPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.DEEP_OCEAN, Biome.OCEAN};

    /**
     * Creates a populator specialized for the ocean.
     */
    public OceanPopulator() {
        surfaceCaveDecorator.setAmount(0);
        entityDecorators.clear();
        // todo: squid decorator
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
