package science.atlarge.opencraft.opencraft.generator.decorators.overworld;

import lombok.Data;
import org.bukkit.Chunk;
import org.bukkit.World;
import science.atlarge.opencraft.opencraft.generator.decorators.BlockDecorator;
import science.atlarge.opencraft.opencraft.generator.objects.Flower;
import science.atlarge.opencraft.opencraft.generator.objects.FlowerType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FlowerDecorator extends BlockDecorator {

    private List<FlowerDecoration> flowers;

    public final void setFlowers(FlowerDecoration... flowers) {
        this.flowers = Arrays.asList(flowers);
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(world.getHighestBlockYAt(sourceX, sourceZ) + 32);

        // the flower can change on each decoration pass
        FlowerType flower = getRandomFlower(random, flowers);
        if (flower != null) {
            new Flower(flower).generate(world, random, sourceX, sourceY, sourceZ);
        }
    }

    private FlowerType getRandomFlower(Random random, List<FlowerDecoration> decorations) {
        int totalWeight = 0;
        for (FlowerDecoration decoration : decorations) {
            totalWeight += decoration.getWeight();
        }
        int weight = random.nextInt(totalWeight);
        for (FlowerDecoration decoration : decorations) {
            weight -= decoration.getWeight();
            if (weight < 0) {
                return decoration.getFlower();
            }
        }
        return null;
    }

    @Data
    public static final class FlowerDecoration {
        private final FlowerType flower;
        private final int weight;
    }
}
