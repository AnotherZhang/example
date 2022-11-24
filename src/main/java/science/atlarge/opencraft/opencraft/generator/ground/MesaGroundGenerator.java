package science.atlarge.opencraft.opencraft.generator.ground;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.material.MaterialData;
import science.atlarge.opencraft.opencraft.util.noise.SimplexOctaveGenerator;

import java.util.Arrays;
import java.util.Random;

public class MesaGroundGenerator extends GroundGenerator {

    protected static final MaterialData RED_SAND = new MaterialData(Material.SAND, (byte) 1);
    protected static final MaterialData ORANGE_STAINED_CLAY = new MaterialData(
            Material.STAINED_CLAY, (byte) 1);

    private final MesaType type;
    private final int[] colorLayer = new int[64];
    private MaterialData topMaterial;
    private MaterialData groundMaterial;
    private SimplexOctaveGenerator colorNoise;
    private SimplexOctaveGenerator canyonHeightNoise;
    private SimplexOctaveGenerator canyonScaleNoise;
    private long seed;

    public MesaGroundGenerator() {
        this(MesaType.NORMAL);
    }

    /**
     * Creates a ground generator for mesa biomes.
     *
     * @param type the type of mesa biome to generate
     */
    public MesaGroundGenerator(MesaType type) {
        this.type = type;
        topMaterial = RED_SAND;
        groundMaterial = ORANGE_STAINED_CLAY;
    }

    private void initialize(long seed) {
        if (seed != this.seed || colorNoise == null || canyonScaleNoise == null
                || canyonHeightNoise == null) {
            Random random = new Random(seed);
            colorNoise = new SimplexOctaveGenerator(random, 1);
            colorNoise.setScale(1 / 512.0D);
            initializeColorLayers(random);

            canyonHeightNoise = new SimplexOctaveGenerator(random, 4);
            canyonHeightNoise.setScale(1 / 4.0D);
            canyonScaleNoise = new SimplexOctaveGenerator(random, 1);
            canyonScaleNoise.setScale(1 / 512.0D);
            this.seed = seed;
        }
    }

    @Override
    public void generateTerrainColumn(
            ChunkData chunkData,
            World world,
            Random random,
            int x,
            int z,
            Biome biome,
            double surfaceNoise
    ) {
        initialize(world.getSeed());

        int seaLevel = world.getSeaLevel();

        MaterialData topMat = topMaterial;

        int surfaceHeight = Math.max((int) (surfaceNoise / 3.0D + 3.0D + random.nextDouble() * 0.25D), 1);
        boolean colored = Math.cos(surfaceNoise / 3.0D * Math.PI) <= 0;
        double bryceCanyonHeight = 0;
        if (type == MesaType.BRYCE) {
            int noiseX = (x & 0xFFFFFFF0) + (z & 0xF);
            int noiseZ = (z & 0xFFFFFFF0) + (x & 0xF);
            double noiseCanyonHeight = Math.min(
                    Math.abs(surfaceNoise),
                    canyonHeightNoise.noise(noiseX, noiseZ, 0.5D, 2.0D)
            );
            if (noiseCanyonHeight > 0) {
                double heightScale = Math.abs(canyonScaleNoise.noise(noiseX, noiseZ, 0.5D, 2.0D));
                bryceCanyonHeight = Math.pow(noiseCanyonHeight, 2) * 2.5D;
                double maxHeight = Math.ceil(50 * heightScale) + 14;
                if (bryceCanyonHeight > maxHeight) {
                    bryceCanyonHeight = maxHeight;
                }
                bryceCanyonHeight += seaLevel;
            }
        }

        int chunkX = x;
        int chunkZ = z;
        x &= 0xF;
        z &= 0xF;

        int deep = -1;
        int upper = 0;
        int lower = 0;
        int topHeight = -1;
        for (int y = 255; y >= 0; y--) {

            // Fill in the gaps.
            if (y < (int) bryceCanyonHeight && chunkData.getType(x, y, z) == Material.AIR) {
                chunkData.setBlock(x, y, z, Material.STONE);
            }

            // Place bedrock.
            if (y <= random.nextInt(5)) {
                chunkData.setBlock(x, y, z, Material.BEDROCK);
                continue;
            }

            Material material = chunkData.getType(x, y, z);

            // Reset the surface depth whenever we hit air.
            if (material == Material.AIR) {
                deep = -1;

            } else if (material != Material.STONE) {
                continue;
            }

            // If the current block is air.
            if (deep == -1) {

                deep = surfaceHeight + Math.max(0, y - seaLevel - 1);

                topHeight = y - 1;

                // Select biome
                if (type == MesaType.FOREST && y > seaLevel + 22 + (surfaceHeight << 1)) {
                    topMat = colored ? GRASS : COARSE_DIRT;
                    upper = 255;
                    lower = seaLevel + surfaceHeight;

                } else {
                    topMat = topMaterial;
                    upper = seaLevel + surfaceHeight + 2;
                    lower = seaLevel - 2;
                }

            } else if (deep > 0) {

                deep--;

                if (y >= lower && y == topHeight && y <= upper) {
                    chunkData.setBlock(x, y, z, topMat);

                } else if (y >= lower && y >= topHeight - 5 && y <= upper) {
                    chunkData.setBlock(x, y, z, groundMaterial);

                } else {
                    int noise = (int) Math.round(colorNoise.noise(chunkX, chunkZ, 0.5D, 2.0D) * 2.0D);
                    int color = colorLayer[(y + noise) % colorLayer.length];
                    setColoredGroundLayer(chunkData, x, y, z, color);
                }
            }
        }
    }

    private void setColoredGroundLayer(ChunkData chunkData, int x, int y, int z, int color) {
        if (color >= 0) {
            chunkData.setBlock(x, y, z, new MaterialData(Material.STAINED_CLAY, (byte) color));
        } else {
            chunkData.setBlock(x, y, z, Material.HARD_CLAY);
        }
    }

    private void setRandomLayerColor(Random random, int minLayerCount, int minLayerHeight, int color) {
        for (int i = 0; i < random.nextInt(4) + minLayerCount; i++) {
            int j = random.nextInt(colorLayer.length);
            int k = 0;
            while (k < random.nextInt(3) + minLayerHeight && j < colorLayer.length) {
                colorLayer[j++] = color;
                k++;
            }
        }
    }

    private void initializeColorLayers(Random random) {
        Arrays.fill(colorLayer, -1); // hard clay, other values are stained clay
        int i = 0;
        while (i < colorLayer.length) {
            i += random.nextInt(5) + 1;
            if (i < colorLayer.length) {
                colorLayer[i++] = 1; // orange
            }
        }
        setRandomLayerColor(random, 2, 1, 4); // yellow
        setRandomLayerColor(random, 2, 2, 12); // brown
        setRandomLayerColor(random, 2, 1, 14); // red
        int j = 0;
        for (i = 0; i < random.nextInt(3) + 3; i++) {
            j += random.nextInt(16) + 4;
            if (j >= colorLayer.length) {
                break;
            }
            if (random.nextInt(2) == 0 || j < colorLayer.length - 1 && random.nextInt(2) == 0) {
                colorLayer[j - 1] = 8; // light gray
            } else {
                colorLayer[j] = 0; // white
            }
        }
    }

    public enum MesaType {
        NORMAL,
        BRYCE,
        FOREST
    }
}
