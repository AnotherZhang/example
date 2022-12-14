package science.atlarge.opencraft.opencraft.generator.objects;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.DoublePlant;
import org.bukkit.material.types.DoublePlantSpecies;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;

import java.util.Random;

public class DoubleTallPlant implements TerrainObject {

    private final DoublePlantSpecies species;

    public DoubleTallPlant(DoublePlantSpecies species) {
        this.species = species;
    }

    /**
     * Generates up to 64 plants around the given point.
     *
     * @return true if at least one plant was successfully generated
     */
    @Override
    public boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        boolean placed = false;
        for (int i = 0; i < 64; i++) {
            int x = sourceX + random.nextInt(8) - random.nextInt(8);
            int z = sourceZ + random.nextInt(8) - random.nextInt(8);
            int y = sourceY + random.nextInt(4) - random.nextInt(4);

            Block block = world.getBlockAt(x, y, z);
            Block topBlock = block.getRelative(BlockFace.UP);
            if (y < 255 && block.isEmpty() && topBlock.isEmpty()
                    && block.getRelative(BlockFace.DOWN).getType() == Material.GRASS) {
                GlowBlockState state = (GlowBlockState) block.getState();
                state.setType(Material.DOUBLE_PLANT);
                state.setData(new DoublePlant(species));
                state.updateNoBroadcast(true, true);
                state = (GlowBlockState) topBlock.getState();
                state.setType(Material.DOUBLE_PLANT);
                state.setData(new DoublePlant(DoublePlantSpecies.PLANT_APEX));
                state.updateNoBroadcast(true, true);
                placed = true;
            }
        }
        return placed;
    }
}
