package science.atlarge.opencraft.opencraft.generator.populators.overworld;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import science.atlarge.opencraft.opencraft.generator.decorators.overworld.IceDecorator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class IcePlainsSpikesPopulator extends IcePlainsPopulator {

    protected final IceDecorator iceDecorator = new IceDecorator();

    public IcePlainsSpikesPopulator() {
        tallGrassDecorator.setAmount(0);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(Biome.MUTATED_ICE_FLATS));
    }

    @Override
    public void populateOnGround(World world, Random random, Chunk chunk) {
        iceDecorator.populate(world, random, chunk);
        super.populateOnGround(world, random, chunk);
    }
}
