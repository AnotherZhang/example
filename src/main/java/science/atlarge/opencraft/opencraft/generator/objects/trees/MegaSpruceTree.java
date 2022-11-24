package science.atlarge.opencraft.opencraft.generator.objects.trees;

import science.atlarge.opencraft.opencraft.util.BlockStateDelegate;

import java.util.Random;

public class MegaSpruceTree extends MegaPineTree {

    /**
     * Initializes this tree, preparing it to attempt to generate.
     *
     * @param random   the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     *                 blocks
     */
    public MegaSpruceTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setLeavesHeight(leavesHeight + 10);
    }
}
