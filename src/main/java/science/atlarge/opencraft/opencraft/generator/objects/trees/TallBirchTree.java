package science.atlarge.opencraft.opencraft.generator.objects.trees;

import science.atlarge.opencraft.opencraft.util.BlockStateDelegate;

import java.util.Random;

public class TallBirchTree extends BirchTree {

    public TallBirchTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setHeight(height + random.nextInt(7));
    }
}
