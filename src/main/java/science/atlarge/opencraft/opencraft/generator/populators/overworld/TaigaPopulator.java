package science.atlarge.opencraft.opencraft.generator.populators.overworld;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.material.types.DoublePlantSpecies;
import science.atlarge.opencraft.opencraft.generator.decorators.overworld.DoublePlantDecorator.DoublePlantDecoration;
import science.atlarge.opencraft.opencraft.generator.decorators.overworld.MushroomDecorator;
import science.atlarge.opencraft.opencraft.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import science.atlarge.opencraft.opencraft.generator.objects.trees.RedwoodTree;
import science.atlarge.opencraft.opencraft.generator.objects.trees.TallRedwoodTree;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class TaigaPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.TAIGA, Biome.TAIGA_HILLS, Biome.MUTATED_TAIGA,
            Biome.TAIGA_COLD,
            Biome.TAIGA_COLD_HILLS, Biome.MUTATED_TAIGA_COLD};
    private static final DoublePlantDecoration[] DOUBLE_PLANTS = {
            new DoublePlantDecoration(DoublePlantSpecies.LARGE_FERN, 1)};
    private static final TreeDecoration[] TREES = {new TreeDecoration(RedwoodTree::new, 2),
            new TreeDecoration(TallRedwoodTree::new, 1)};

    protected final MushroomDecorator taigaBrownMushroomDecorator = new MushroomDecorator(
            Material.BROWN_MUSHROOM);
    protected final MushroomDecorator taigaRedMushroomDecorator = new MushroomDecorator(
            Material.RED_MUSHROOM);

    /**
     * Creates a populator specialized for Taiga, Taiga Hills and Taiga M, and their Cold variants.
     */
    public TaigaPopulator() {
        doublePlantDecorator.setAmount(7);
        doublePlantDecorator.setDoublePlants(DOUBLE_PLANTS);
        treeDecorator.setAmount(10);
        treeDecorator.setTrees(TREES);
        tallGrassDecorator.setFernDensity(0.8D);
        deadBushDecorator.setAmount(1);
        taigaBrownMushroomDecorator.setAmount(1);
        taigaBrownMushroomDecorator.setUseFixedHeightRange();
        taigaBrownMushroomDecorator.setDensity(0.25D);
        taigaRedMushroomDecorator.setAmount(1);
        taigaRedMushroomDecorator.setDensity(0.125D);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }

    @Override
    public void populateOnGround(World world, Random random, Chunk chunk) {
        super.populateOnGround(world, random, chunk);
        taigaBrownMushroomDecorator.populate(world, random, chunk);
        taigaRedMushroomDecorator.populate(world, random, chunk);
    }
}
