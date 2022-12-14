package science.atlarge.opencraft.opencraft.chunk;

import com.google.gson.annotations.Expose;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.event.world.ChunkUnloadEvent;
import science.atlarge.opencraft.opencraft.EventFactory;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.block.ItemTable;
import science.atlarge.opencraft.opencraft.block.blocktype.BlockType;
import science.atlarge.opencraft.opencraft.block.entity.BlockEntity;
import science.atlarge.opencraft.opencraft.entity.GlowEntity;
import science.atlarge.opencraft.opencraft.net.message.play.game.ChunkDataMessage;
import science.atlarge.opencraft.opencraft.population.serialization.json.annotations.ExposeClass;
import science.atlarge.opencraft.opencraft.util.Coordinates;
import science.atlarge.opencraft.opencraft.util.IntCoordinates2D;
import science.atlarge.opencraft.opencraft.util.TickUtil;
import science.atlarge.opencraft.opencraft.util.config.ServerConfig;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Represents a chunk of the map.
 *
 * @author Graham Edgecombe
 */
@ExposeClass
public class GlowChunk implements Chunk {

    /**
     * The width of a chunk (x axis).
     */
    public static final int WIDTH = 16;

    /**
     * The height of a chunk (z axis).
     */
    public static final int HEIGHT = 16;

    /**
     * The depth of a chunk (y axis).
     */
    public static final int DEPTH = 256;

    /**
     * The Y depth of a single chunk section.
     */
    public static final int SEC_DEPTH = 16;

    /**
     * The number of chunk sections in a single chunk column.
     */
    public static final int SEC_COUNT = DEPTH / SEC_DEPTH;

    /**
     * Used in a kludge to prevent repeated serialization of chunk messages.
     * This cache saves chunk messages, which can be useful for chunks that need to be sent multiple times,
     * such as those around spawn. Using this cache breaks world modifiability.
     */
    private static final Map<Integer, Map<Integer, ChunkDataMessage>> chunkCache = new ConcurrentHashMap<>();

    /**
     * The world of this chunk.
     */
    @Getter
    @Setter
    private GlowWorld world;

    /**
     * The coordinates of this chunk
     */
    @Getter
    @Expose
    private final IntCoordinates2D coordinates;

    /**
     * The block entities that reside in this chunk.
     */
    @Expose
    private final HashMap<Integer, BlockEntity> blockEntities = new HashMap<>();

    /**
     * The entities that reside in this chunk.
     */
    private final Set<GlowEntity> entities = ConcurrentHashMap.newKeySet(4);

    /**
     * The array of chunk sections this chunk contains, or null if it is unloaded.
     *
     * @return The chunk sections array.
     */
    @Getter
    @Expose
    private ChunkSection[] sections;

    /**
     * The array of biomes this chunk contains, or null if it is unloaded.
     */
    @Expose
    private byte[] biomes;

    /**
     * The height map values values of each column, or null if it is unloaded. The height for a
     * column is one plus the y-index of the highest non-air block in the column.
     */
    @Expose
    private byte[] heightMap;

    /**
     * Whether the chunk has been populated by special features. Used in map generation.
     *
     * @param populated Population status.
     * @return Population status.
     */
    @Getter
    @Setter
    @Expose
    private boolean populated;

    @Setter
    private int isSlimeChunk = -1;
    @Getter
    @Setter
    private long inhabitedTime;

    /**
     * Creates a new chunk with a specified X and Z coordinate.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     */
    public GlowChunk(GlowWorld world, int x, int z) {
        this(world, new IntCoordinates2D(x, z));
    }

    /**
     * Creates a new chunk with a specified X and Z coordinate.
     *
     * @param world       the world the chunk belongs to (e.g., overworld, nether, etc.).
     * @param coordinates this chunks coordinates.
     */
    public GlowChunk(GlowWorld world, IntCoordinates2D coordinates) {
        this.world = world;
        this.coordinates = coordinates;
    }

    // ======== Basic stuff ========

    @Override
    public String toString() {
        return "GlowChunk{world=" + world.getName() + ",x=" + coordinates.getX() + ",z=" + coordinates.getZ() + '}';
    }

    @Override
    public GlowBlock getBlock(int x, int y, int z) {
        return new GlowBlock(this, this.coordinates.getX() << 4 | x & 0xf, y & 0xff,
                this.coordinates.getZ() << 4 | z & 0xf);
    }

    @Override
    public Entity[] getEntities() {
        return entities.toArray(new Entity[entities.size()]);
    }

    public Collection<GlowEntity> getRawEntities() {
        return entities;
    }

    @Override
    @Deprecated
    public GlowBlockState[] getTileEntities() {
        return getBlockEntities();
    }

    /**
     * Returns the states of the block entities (e.g. container blocks) in this chunk.
     *
     * @return the states of the block entities in this chunk
     */
    public GlowBlockState[] getBlockEntities() {
        List<GlowBlockState> states = new ArrayList<>(blockEntities.size());
        for (BlockEntity blockEntity : blockEntities.values()) {
            GlowBlockState state = blockEntity.getState();
            if (state != null) {
                states.add(state);
            }
        }

        return states.toArray(new GlowBlockState[0]);
    }

    public Collection<BlockEntity> getRawBlockEntities() {
        return Collections.unmodifiableCollection(blockEntities.values());
    }

    /**
     * Formula taken from Minecraft Gamepedia.
     * https://minecraft.gamepedia.com/Slime#.22Slime_chunks.22
     */
    @Override
    public boolean isSlimeChunk() {
        int x = coordinates.getX();
        int z = coordinates.getZ();
        if (isSlimeChunk == -1) {
            boolean isSlimeChunk = new Random(this.world.getSeed()
                    + ((long) x * x * 0x4c1906)
                    + (x * 0x5ac0dbL)
                    + ((long) z * z) * 0x4307a7L
                    + (z * 0x5f24fL) ^ 0x3ad8025f).nextInt(10) == 0;

            this.isSlimeChunk = (isSlimeChunk ? 1 : 0);
        }

        return this.isSlimeChunk == 1;
    }

    @Override
    public GlowChunkSnapshot getChunkSnapshot() {
        return getChunkSnapshot(true, false, false);
    }

    @Override
    public GlowChunkSnapshot getChunkSnapshot(boolean includeMaxBlockY, boolean includeBiome,
            boolean includeBiomeTempRain) {
        return new GlowChunkSnapshot(coordinates.getX(), coordinates.getZ(), world, sections,
                includeMaxBlockY ? heightMap.clone() : null, includeBiome ? biomes.clone() : null,
                includeBiomeTempRain, isSlimeChunk());
    }

    @Override
    public boolean isLoaded() {
        return sections != null;
    }

    @Override
    public boolean load() {
        return load(true);
    }

    @Override
    public boolean load(boolean generate) {
        return isLoaded() || world.getChunkManager().loadChunk(this, generate);
    }

    @Override
    public boolean unload() {
        return unload(true, true);
    }

    @Override
    public boolean unload(boolean save) {
        return unload(save, true);
    }

    @Override
    public boolean unload(boolean save, boolean safe) {
        if (!isLoaded()) {
            return true;
        }

        if (safe && world.isChunkInUse(coordinates)) {
            return false;
        }

        if (save && !world.getChunkManager().performSave(this)) {
            return false;
        }

        if (EventFactory.getInstance()
                .callEvent(new ChunkUnloadEvent(this)).isCancelled()) {
            return false;
        }

        sections = null;
        biomes = null;
        heightMap = null;
        blockEntities.clear();
        if (save) {
            for (GlowEntity entity : entities) {
                entity.remove();
            }
            entities.clear();
        }
        return true;
    }

    // ======== Helper Functions ========

    /**
     * Initialize this chunk from the given sections.
     *
     * @param initSections The {@link ChunkSection}s to use. Should have a length of {@value
     *                     #SEC_COUNT}.
     */
    public void initializeSections(ChunkSection[] initSections) {
        if (isLoaded()) {
            GlowServer.logger.log(Level.SEVERE,
                    "Tried to initialize already loaded chunk (" + getX() + "," + getZ() + ")",
                    new Throwable());
            return;
        }
        if (initSections.length != SEC_COUNT) {
            GlowServer.logger.log(Level.WARNING,
                    "Got an unexpected section length - wanted " + SEC_COUNT + ", but length was "
                            + initSections.length,
                    new Throwable());
        }
        //GlowServer.logger.log(Level.INFO, "Initializing chunk ({0},{1})", new Object[]{x, z});

        sections = new ChunkSection[SEC_COUNT];
        biomes = new byte[WIDTH * HEIGHT];
        heightMap = new byte[WIDTH * HEIGHT];

        for (int y = 0; y < SEC_COUNT && y < initSections.length; y++) {
            if (initSections[y] != null) {
                initializeSection(y, initSections[y]);
            }
        }
    }

    private void initializeSection(int y, ChunkSection section) {
        sections[y] = section;
    }

    /**
     * If needed, create a new block entity at the given location.
     *
     * @param cx   the X coordinate of the BlockEntity
     * @param cy   the Y coordinate of the BlockEntity
     * @param cz   the Z coordinate of the BlockEntity
     * @param type the type of BlockEntity
     * @return The BlockEntity that was created.
     */
    public BlockEntity createEntity(int cx, int cy, int cz, int type) {
        Material material = Material.getMaterial(type);

        switch (material) {
            case SIGN:
            case SIGN_POST:
            case WALL_SIGN:
            case BED_BLOCK:
            case CHEST:
            case TRAPPED_CHEST:
            case BURNING_FURNACE:
            case FURNACE:
            case DISPENSER:
            case DROPPER:
            case END_GATEWAY:
            case HOPPER:
            case MOB_SPAWNER:
            case NOTE_BLOCK:
            case JUKEBOX:
            case BREWING_STAND:
            case SKULL:
            case COMMAND:
            case COMMAND_CHAIN:
            case COMMAND_REPEATING:
            case BEACON:
            case BANNER:
            case WALL_BANNER:
            case STANDING_BANNER:
            case FLOWER_POT:
            case STRUCTURE_BLOCK:
            case WHITE_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case SILVER_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case BLACK_SHULKER_BOX:
            case ENCHANTMENT_TABLE:
            case ENDER_CHEST:
            case DAYLIGHT_DETECTOR:
            case DAYLIGHT_DETECTOR_INVERTED:
            case REDSTONE_COMPARATOR_OFF:
            case REDSTONE_COMPARATOR_ON:
                BlockType blockType = ItemTable.instance().getBlock(material);
                if (blockType == null) {
                    return null;
                }

                try {
                    BlockEntity entity = blockType.createBlockEntity(this, cx, cy, cz);
                    if (entity == null) {
                        return null;
                    }

                    blockEntities.put(coordinateToIndex(cx, cz, cy), entity);
                    return entity;
                } catch (Exception ex) {
                    GlowServer.logger
                            .log(Level.SEVERE, "Unable to initialize block entity for " + type, ex);
                    return null;
                }
            default:
                return null;
        }
    }

    // ======== Data access ========

    @Override
    public int getX() {
        return coordinates.getX();
    }

    @Override
    public int getZ() {
        return coordinates.getZ();
    }

    /**
     * Attempt to get the ChunkSection at the specified height.
     *
     * @param y the y value.
     * @return The ChunkSection, or null if it is empty.
     */
    private ChunkSection getSection(int y) {
        int idx = y >> 4;
        if (y < 0 || y >= DEPTH || !load() || idx >= sections.length) {
            return null;
        }
        return sections[idx];
    }

    /**
     * Attempt to get the block entity located at the given coordinates.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return A GlowBlockState if the entity exists, or null otherwise.
     */
    public BlockEntity getEntity(int x, int y, int z) {
        if (y >= DEPTH || y < 0) {
            return null;
        }
        load();
        return blockEntities.get(coordinateToIndex(x, z, y));
    }

    /**
     * Gets the type of a block within this chunk.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The type.
     */
    public int getType(int x, int z, int y) {
        ChunkSection section = getSection(y);
        return section == null ? 0 : section.getType(x, y, z) >> 4;
    }

    /**
     * Sets the type of a block within this chunk.
     *
     * @param x    The X coordinate.
     * @param z    The Z coordinate.
     * @param y    The Y coordinate.
     * @param type The type.
     */
    public void setType(int x, int z, int y, int type) {
        if (type < 0 || type > 0xfff) {
            throw new IllegalArgumentException("Block type out of range: " + type);
        }

        ChunkSection section = getSection(y);
        if (section == null) {
            if (type == 0) {
                // don't need to create chunk for air
                return;
            } else {
                // create new ChunkSection for this y coordinate
                int idx = y >> 4;
                if (y < 0 || y >= DEPTH || idx >= sections.length) {
                    // y is out of range somehow
                    return;
                }
                sections[idx] = section = new ChunkSection();
            }
        }

        // destroy any block entity there
        int blockEntityIndex = coordinateToIndex(x, z, y);
        if (blockEntities.containsKey(blockEntityIndex)) {
            blockEntities.remove(blockEntityIndex).destroy();
        }

        // update the air count and height map
        int heightIndex = z * WIDTH + x;
        if (type == 0) {
            if (heightMap[heightIndex] == y + 1) {
                // erased just below old height map -> lower
                heightMap[heightIndex] = (byte) lowerHeightMap(x, y, z);
            }
        } else {
            if (heightMap[heightIndex] <= y) {
                // placed between old height map and top -> raise
                heightMap[heightIndex] = (byte) Math.min(y + 1, 255);
            }
        }
        // update the type - also sets metadata to 0
        section.setType(x, y, z, (char) (type << 4));

        if (section.isEmpty()) {
            // destroy the empty section
            sections[y / SEC_DEPTH] = null;
            return;
        }

        // create a new block entity if we need
        createEntity(x, y, z, type);
    }

    /**
     * Scan downwards to determine the new height map value.
     */
    private int lowerHeightMap(int x, int y, int z) {
        for (--y; y >= 0; --y) {
            if (getType(x, z, y) != 0) {
                break;
            }
        }
        return y + 1;
    }

    /**
     * Gets the metadata of a block within this chunk.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The metadata.
     */
    public int getMetaData(int x, int z, int y) {
        ChunkSection section = getSection(y);
        return section == null ? 0 : section.getType(x, y, z) & 0xF;
    }

    /**
     * Sets the metadata of a block within this chunk.
     *
     * @param x        The X coordinate.
     * @param z        The Z coordinate.
     * @param y        The Y coordinate.
     * @param metaData The metadata.
     */
    public void setMetaData(int x, int z, int y, int metaData) {
        if (metaData < 0 || metaData >= 16) {
            throw new IllegalArgumentException("Metadata out of range: " + metaData);
        }
        ChunkSection section = getSection(y);
        if (section == null) {
            return;  // can't set metadata on an empty section
        }
        int type = section.getType(x, y, z);
        if (type == 0) {
            return;  // can't set metadata on air
        }
        section.setType(x, y, z, (char) (type & 0xfff0 | metaData));
    }

    /**
     * Gets the sky light level of a block within this chunk.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The sky light level.
     */
    public byte getSkyLight(int x, int z, int y) {
        ChunkSection section = getSection(y);
        return section == null ? ChunkSection.EMPTY_SKYLIGHT : section.getSkyLight(x, y, z);
    }

    /**
     * Sets the sky light level of a block within this chunk.
     *
     * @param x        The X coordinate.
     * @param z        The Z coordinate.
     * @param y        The Y coordinate.
     * @param skyLight The sky light level.
     */
    public void setSkyLight(int x, int z, int y, int skyLight) {
        ChunkSection section = getSection(y);
        if (section == null) {
            return;  // can't set light on an empty section
        }
        section.setSkyLight(x, y, z, (byte) skyLight);
    }

    /**
     * Gets the block light level of a block within this chunk.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The block light level.
     */
    public byte getBlockLight(int x, int z, int y) {
        ChunkSection section = getSection(y);
        return section == null ? ChunkSection.EMPTY_BLOCK_LIGHT : section.getBlockLight(x, y, z);
    }

    /**
     * Sets the block light level of a block within this chunk.
     *
     * @param x          The X coordinate.
     * @param z          The Z coordinate.
     * @param y          The Y coordinate.
     * @param blockLight The block light level.
     */
    public void setBlockLight(int x, int z, int y, int blockLight) {
        ChunkSection section = getSection(y);
        if (section == null) {
            return;  // can't set light on an empty section
        }
        section.setBlockLight(x, y, z, (byte) blockLight);
    }

    /**
     * Gets the biome of a column within this chunk.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return The biome.
     */
    public int getBiome(int x, int z) {
        if (biomes == null && !load()) {
            return 0;
        }
        return biomes[z * WIDTH + x] & 0xFF;
    }

    /**
     * Sets the biome of a column within this chunk.
     *
     * @param x     The X coordinate.
     * @param z     The Z coordinate.
     * @param biome The biome.
     */
    public void setBiome(int x, int z, int biome) {
        if (biomes == null) {
            return;
        }
        biomes[z * WIDTH + x] = (byte) biome;
    }

    /**
     * Set the entire biome array of this chunk.
     *
     * @param newBiomes The biome array.
     */
    public void setBiomes(byte... newBiomes) {
        if (biomes == null) {
            throw new IllegalStateException("Must initialize chunk first");
        }
        if (newBiomes.length != biomes.length) {
            throw new IllegalArgumentException("Biomes array not of length " + biomes.length);
        }
        System.arraycopy(newBiomes, 0, biomes, 0, biomes.length);
    }

    /**
     * Get the height map value of a column within this chunk.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return The height map value.
     */
    public int getHeight(int x, int z) {
        if (heightMap == null && !load()) {
            return 0;
        }
        return heightMap[z * WIDTH + x] & 0xff;
    }

    /**
     * Computes the regional difficulty.
     * Not used at the moment.
     *
     * @return regional difficulty.
     */
    public double getRegionalDifficulty() {
        final double moonPhase = world.getMoonPhase();
        final long worldTime = world.getFullTime();
        final Difficulty worldDifficulty = world.getDifficulty();

        double totalTimeFactor;
        if (worldTime > (21 * TickUtil.TICKS_PER_HOUR)) {
            totalTimeFactor = 0.25;
        } else if (worldTime < TickUtil.TICKS_PER_HOUR) {
            totalTimeFactor = 0;
        } else {
            totalTimeFactor = (worldTime - TickUtil.TICKS_PER_HOUR) / 5760000d;
        }

        double chunkFactor;
        if (inhabitedTime > (50 * TickUtil.TICKS_PER_HOUR)) {
            chunkFactor = 1;
        } else {
            chunkFactor = inhabitedTime / 3600000d;
        }

        if (worldDifficulty != Difficulty.HARD) {
            chunkFactor *= 3d / 4d;
        }

        if (moonPhase / 4 > totalTimeFactor) {
            chunkFactor += totalTimeFactor;
        } else {
            chunkFactor += moonPhase / 4;
        }

        if (worldDifficulty == Difficulty.EASY) {
            chunkFactor /= 2;
        }

        double regionalDifficulty = 0.75 + totalTimeFactor + chunkFactor;

        if (worldDifficulty == Difficulty.NORMAL) {
            regionalDifficulty *= 2;
        }
        if (worldDifficulty == Difficulty.HARD) {
            regionalDifficulty *= 3;
        }

        return regionalDifficulty;
    }

    /**
     * Compute the normalized regional difficulty.
     * Not used at the moment.
     *
     * @return normalized regional difficulty.
     */
    public double getClampedRegionalDifficulty() {
        final double rd = getRegionalDifficulty();

        if (rd < 2.0) {
            return 0;
        } else if (rd > 4.0) {
            return 1;
        } else {
            return (rd - 2) / 2;
        }
    }

    /**
     * Set the entire height map of this chunk.
     *
     * @param newHeightMap The height map.
     */
    public void setHeightMap(int... newHeightMap) {
        if (heightMap == null) {
            throw new IllegalStateException("Must initialize chunk first");
        }
        if (newHeightMap.length != heightMap.length) {
            throw new IllegalArgumentException("Height map not of length " + heightMap.length);
        }
        for (int i = 0; i < heightMap.length; ++i) {
            heightMap[i] = (byte) newHeightMap[i];
        }
    }

    /**
     * Set this chunk's sections from a given array of chunk sections.
     *
     * @param newSections The given chunk sections.
     */
    public void setSections(ChunkSection... newSections) {
        if (sections == null) {
            throw new IllegalStateException("Must initialize chunk first");
        }
        if (newSections.length != sections.length) {
            throw new IllegalArgumentException("Sections not of length " + sections.length);
        }
        sections = newSections;
    }

    // ======== Helper functions ========

    /**
     * Automatically fill the height map after chunks have been initialized.
     */
    public void automaticHeightMap() {
        // determine max Y chunk section at a time
        int sy = sections.length - 1;
        for (; sy >= 0; --sy) {
            if (sections[sy] != null) {
                break;
            }
        }
        int y = (sy + 1) << 4;
        for (int x = 0; x < WIDTH; ++x) {
            for (int z = 0; z < HEIGHT; ++z) {
                heightMap[z * WIDTH + x] = (byte) lowerHeightMap(x, y, z);
            }
        }
    }

    /**
     * Converts a three-dimensional coordinate to an index within the one-dimensional arrays.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The index within the arrays.
     */
    private int coordinateToIndex(int x, int z, int y) {
        if (x < 0 || z < 0 || y < 0 || x >= WIDTH || z >= HEIGHT || y >= DEPTH) {
            throw new IndexOutOfBoundsException(
                    "Coords (x=" + x + ",y=" + y + ",z=" + z + ") invalid");
        }

        return (y * HEIGHT + z) * WIDTH + x;
    }

    /**
     * Creates a new {@link ChunkDataMessage} which can be sent to a client to stream this entire
     * chunk to them.
     *
     * @return The {@link ChunkDataMessage}.
     */
    public ChunkDataMessage toMessage() {
        // this may need to be changed to "true" depending on resolution of
        // some inconsistencies on the wiki
        return toMessage(world.getEnvironment() == Environment.NORMAL);
    }

    /**
     * Creates a new {@link ChunkDataMessage} which can be sent to a client to stream this entire
     * chunk to them.
     *
     * @param skylight Whether to include skylight data.
     * @return The {@link ChunkDataMessage}.
     */
    public ChunkDataMessage toMessage(boolean skylight) {
        return toMessage(skylight, true);
    }

    /**
     * Creates a new {@link ChunkDataMessage} which can be sent to a client to stream parts of this
     * chunk to them.
     *
     * @param skylight    Whether to include skylight data.
     * @param entireChunk Whether to send all chunk sections.
     * @return The {@link ChunkDataMessage}.
     */
    public ChunkDataMessage toMessage(boolean skylight, boolean entireChunk) {
        if (ServerConfig.getInstance().getBoolean(ServerConfig.Key.OPENCRAFT_KLUDGE_CHUNKCACHE)) {
            ChunkDataMessage cachedMessage =
                    chunkCache.computeIfAbsent(getX(), x -> new ConcurrentHashMap<>()).get(this.getZ());
            if (cachedMessage != null) {
                return cachedMessage;
            }
        }

        load();
        int sectionBitmask = 0;

        // filter sectionBitmask based on actual chunk contents
        if (sections != null) {
            int maxBitmask = (1 << sections.length) - 1;
            if (entireChunk) {
                sectionBitmask = maxBitmask;
            }

            for (int i = 0; i < sections.length; ++i) {
                if (sections[i] == null || sections[i].isEmpty()) {
                    // remove empty sections from bitmask
                    sectionBitmask &= ~(1 << i);
                }
            }
        }

        ByteBuf buf = Unpooled.buffer();

        if (sections != null) {
            // get the list of sections
            for (int i = 0; i < sections.length; ++i) {
                if ((sectionBitmask & 1 << i) == 0) {
                    continue;
                }
                sections[i].writeToBuf(buf, skylight);
            }
        }

        // biomes
        if (entireChunk && biomes != null) {
            buf.writeBytes(biomes);
        }

        Set<CompoundTag> blockEntities = new HashSet<>();
        for (BlockEntity blockEntity : getRawBlockEntities()) {
            CompoundTag tag = new CompoundTag();
            blockEntity.saveNbt(tag);
            blockEntities.add(tag);
        }

        ChunkDataMessage result = new ChunkDataMessage(getX(), getZ(), entireChunk, sectionBitmask, buf, blockEntities);
        if (ServerConfig.getInstance().getBoolean(ServerConfig.Key.OPENCRAFT_KLUDGE_CHUNKCACHE)) {
            chunkCache.computeIfAbsent(getX(), x -> new ConcurrentHashMap<>()).put(getZ(), result);
        }
        return result;
    }

    public void addTick() {
        inhabitedTime++;
    }

    /**
     * Get the coordinates of the center chunk.
     *
     * @return The x and z coordinates at the center of the chunk.
     */
    public Coordinates getCenterCoordinates() {
        // Multiply by 16 and add 8 to get the center of the chunk.
        final int centerX = (getX() << 4) + 8;
        final int centerZ = (getZ() << 4) + 8;

        return new Coordinates(centerX, centerZ);
    }
}
