package science.atlarge.opencraft.opencraft.generator.decorators.nether;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.generator.decorators.BlockDecorator;

import java.util.Arrays;
import java.util.Random;

public class MushroomDecorator extends BlockDecorator {

    private static final Material[] MATERIALS = {Material.NETHERRACK, Material.QUARTZ_ORE,
            Material.SOUL_SAND, Material.GRAVEL};

    private final Material type;

    /**
     * Creates a mushroom decorator for the nether.
     *
     * @param type {@link Material#BROWN_MUSHROOM} or {@link Material#RED_MUSHROOM}
     */
    public MushroomDecorator(Material type) {
        if (type != Material.BROWN_MUSHROOM && type != Material.RED_MUSHROOM) {
            throw new IllegalArgumentException(
                    "MushroomDecorator material must be BROWN_MUSHROOM or RED_MUSHROOM");
        }
        this.type = type;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(128);

        for (int i = 0; i < 64; i++) {
            int x = sourceX + random.nextInt(8) - random.nextInt(8);
            int z = sourceZ + random.nextInt(8) - random.nextInt(8);
            int y = sourceY + random.nextInt(4) - random.nextInt(4);

            Block block = world.getBlockAt(x, y, z);
            Block blockBelow = world.getBlockAt(x, y - 1, z);
            if (y < 128 && block.getType() == Material.AIR && Arrays.asList(MATERIALS)
                    .contains(blockBelow.getType())) {
                GlowBlockState state = (GlowBlockState) block.getState();
                state.setType(type);
                state.setData(new MaterialData(type));
                state.updateNoBroadcast(true, true);
            }
        }
    }
}
