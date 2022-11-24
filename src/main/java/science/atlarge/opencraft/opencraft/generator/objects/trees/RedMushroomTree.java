package science.atlarge.opencraft.opencraft.generator.objects.trees;

import org.bukkit.Material;
import science.atlarge.opencraft.opencraft.util.BlockStateDelegate;

import java.util.Random;

public class RedMushroomTree extends BrownMushroomTree {

    /**
     * Initializes this mushroom, preparing it to attempt to generate.
     *
     * @param random   the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     */
    public RedMushroomTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        type = Material.HUGE_MUSHROOM_2;
    }
}
