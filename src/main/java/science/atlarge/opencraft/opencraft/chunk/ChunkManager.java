package science.atlarge.opencraft.opencraft.chunk;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import science.atlarge.opencraft.opencraft.EventFactory;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.constants.GlowBiome;
import science.atlarge.opencraft.opencraft.generator.GlowChunkData;
import science.atlarge.opencraft.opencraft.generator.GlowChunkGenerator;
import science.atlarge.opencraft.opencraft.generator.biomegrid.MapLayer;
import science.atlarge.opencraft.opencraft.i18n.ConsoleMessages;
import science.atlarge.opencraft.opencraft.io.ChunkIoService;
import science.atlarge.opencraft.opencraft.net.message.play.game.BlockChangeMessage;
import science.atlarge.opencraft.opencraft.population.PopulateInfo;
import science.atlarge.opencraft.opencraft.population.serialization.json.annotations.ExposeClass;
import science.atlarge.opencraft.opencraft.util.IntCoordinates2D;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A class which manages the {@link GlowChunk}s currently loaded in memory.
 *
 * @author Graham Edgecombe
 */
@ExposeClass
public class ChunkManager {

    /**
     * The world this ChunkManager is managing.
     */
    @Setter
    private GlowWorld world;

    /**
     * The chunk I/O service used to read chunks from the disk and write them to the disk.
     */
    private final ChunkIoService service;

    /**
     * The chunk generator used to generate new chunks.
     *
     * @return the chunk generator
     */
    @Getter
    @Expose
    private final ChunkGenerator generator;

    /**
     * The biome maps used to fill chunks biome grid and terrain generation.
     */
    @Expose
    private final MapLayer[] biomeGrid;

    /**
     * A map of chunks currently loaded in memory.
     * DO NOT use this field directly. Use the getter to make sure it is properly initialized.
     */
    private final ConcurrentMap<IntCoordinates2D, GlowChunk> chunks = new ConcurrentHashMap<>();

    /**
     * A set of chunks which are being kept loaded by players or other factors.
     */
    private final Multiset<IntCoordinates2D> lockSet = ConcurrentHashMultiset.create();

    /**
     * Lock to prevent concurrent access to methods of this class.
     * This class can get multiple accesses to the forcePopulation method from ChunkRunnable.
     * TODO This should be refactored. It does not make sense to concurrently generate/populate chunks if the population
     * step must be performed sequentially.
     */
    private final Lock lock;

    /**
     * Creates a new chunk manager with the specified I/O service and world generator.
     *
     * @param world     The chunk manager's world.
     * @param service   The I/O service.
     * @param generator The world generator.
     */
    public ChunkManager(GlowWorld world, ChunkIoService service, ChunkGenerator generator) {
        this.world = world;
        this.service = service;
        this.generator = generator;
        biomeGrid = MapLayer.initialize(world.getSeed(), world.getEnvironment(), world.getWorldType());
        lock = new ReentrantLock();
    }

    /**
     * Gets a chunk object representing the specified coordinates, which might not yet be loaded.
     *
     * @param coordinates the chunk's coordinates.
     * @return The chunk.
     */
    public GlowChunk getChunk(IntCoordinates2D coordinates) {
        return getChunks().computeIfAbsent(coordinates, k -> {
            GlowChunk chunk = new GlowChunk(world, coordinates);
            if (world.isServerless()) {
                generateChunk(chunk);
            }
            return chunk;
        });
    }

    /**
     * Checks if the Chunk at the specified coordinates is loaded.
     *
     * @param coordinates the chunk's coordinates
     * @return true if the chunk is loaded, otherwise false
     */
    public boolean isChunkLoaded(IntCoordinates2D coordinates) {
        GlowChunk chunk = getChunks().computeIfAbsent(coordinates, k -> new GlowChunk(world, coordinates));
        return chunk.isLoaded();
    }

    /**
     * Check whether a chunk has locks on it preventing it from being unloaded.
     *
     * @param coordinates the chunk's coordinates.
     * @return Whether the chunk is in use.
     */
    public boolean isChunkInUse(IntCoordinates2D coordinates) {
        return lockSet.contains(coordinates);
    }

    /**
     * Call the ChunkIoService to load a chunk, optionally generating the chunk.
     *
     * @param coordinates the chunk's coordinates
     * @param generate    Whether to generate the chunk if needed
     * @return True on success, false on failure
     */
    public boolean loadChunk(IntCoordinates2D coordinates, boolean generate) {
        return loadChunk(getChunk(coordinates), generate);
    }

    /**
     * Attempts to load a chunk; handles exceptions.
     *
     * @param chunk    the chunk address
     * @param generate if true, generate the chunk if it's new or the saved copy is corrupted
     * @return true if the chunk was loaded or generated successfully, false otherwise
     */
    public boolean loadChunk(GlowChunk chunk, boolean generate) {
        lock.lock();
        try {

            if (chunk.isLoaded()) {
                return true;
            }

            if (!world.isServerless()) {
                // Read from file
                try {
                    if (service.read(chunk)) {
                        EventFactory.getInstance().callEvent(new ChunkLoadEvent(chunk, false));
                        return true;
                    }
                } catch (IOException e) {
                    ConsoleMessages.Error.Chunk.LOAD_FAILED.log(e, chunk.getX(), chunk.getZ());
                    // an error in chunk reading may have left the chunk in an invalid state
                    // (i.e. double initialization errors), so it's forcibly unloaded here
                    chunk.unload(false, false);

                }

                // stop here if we can't generate
                if (!generate || world.getServer().isGenerationDisabled()) {
                    return false;
                }
            }

            // get generating
            try {
                generateChunk(chunk);
            } catch (Throwable ex) {
                ConsoleMessages.Error.Chunk.GEN_FAILED.log(ex, chunk.getX(), chunk.getZ());
                return false;
            }

            EventFactory.getInstance().callEvent(new ChunkLoadEvent(chunk, true));

            return true;

        } finally {
            lock.unlock();
        }
    }

    /**
     * Unload chunks with no locks on them.
     */
    public void unloadOldChunks() {
        // Lock ChunkManager. Other threads cannot access (most) other ChunkManager methods
        lock.lock();
        try {
            // Get the entries in the chunks field.
            Iterator<Entry<IntCoordinates2D, GlowChunk>> chunksEntryIter = getChunks().entrySet().iterator();
            while (chunksEntryIter.hasNext()) {

                // If the chunk is not in the lockSet, try to unload it
                Entry<IntCoordinates2D, GlowChunk> entry = chunksEntryIter.next();
                if (!lockSet.contains(entry.getKey())) {
                    if (!entry.getValue().unload(true, true)) {
                        ConsoleMessages.Warn.Chunk.UNLOAD_FAILED.log(world.getName(), entry.getKey());
                    }
                }

                if (!entry.getValue().isLoaded()) {
                    chunksEntryIter.remove();
                    lockSet.setCount(entry.getKey(), 0);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Populate a single chunk if needed.
     */
    private void populateChunk(IntCoordinates2D coordinates, boolean force) {
        // If we do not generate chunks, neither should we populate them.
        if (world.getServer().isGenerationDisabled()) {
            return;
        }

        lock.lock();

        // start local chunk population timer
        GlowServer.eventLogger.start(String.format("local_population (%s)", coordinates));

        try {

            GlowChunk chunk = getChunk(coordinates);
            // cancel out if it's already populated
            if (chunk.isPopulated()) {
                return;
            }

            final int x = coordinates.getX();
            final int z = coordinates.getZ();
            // cancel out if the 3x3 around it isn't available
            for (int x2 = x - 1; x2 <= x + 1; x2++) {
                for (int z2 = z - 1; z2 <= z + 1; z2++) {
                    if (!getChunk(new IntCoordinates2D(x2, z2)).isLoaded()) {
                        if (!force || !loadChunk(coordinates, true)) {
                            return;
                        }
                    }
                }
            }

            // it might have loaded since before, so check again that it's not already populated
            if (chunk.isPopulated()) {
                return;
            }
            chunk.setPopulated(true);

            Random random = new Random(world.getSeed());
            long xrand = (random.nextLong() / 2 << 1) + 1;
            long zrand = (random.nextLong() / 2 << 1) + 1;
            random.setSeed(x * xrand + z * zrand ^ world.getSeed());

            for (BlockPopulator p : world.getPopulators()) {
                p.populate(world, random, chunk);
            }

            world.broadcastTemporaryBlockChangeMessages(chunk);

            EventFactory.getInstance().callEvent(new ChunkPopulateEvent(chunk));
        } finally {
            // stop local chunk population timer
            GlowServer.eventLogger.stop(String.format("local_population (%s)", coordinates));

            lock.unlock();
        }
    }

    /**
     * Syncs the chunk back to memory after serverless population has completed.
     *
     * @param coordinates the chunk's coordinates
     * @param chunkData   the output of the serverless population function
     */
    public void syncChunk(IntCoordinates2D coordinates, PopulateInfo.PopulateOutput chunkData) {
        GlowServer.eventLogger.stop(String.format("sync_queue (%s)", coordinates));
        GlowServer.eventLogger.start(String.format("sync_chunk (%s)", coordinates));
        lock.lock();

        try {
            GlowChunk chunk = getChunk(coordinates);

            // cancel out if it's already populated
            if (chunk.isPopulated()) {
                GlowServer.eventLogger.cancel(String.format("sync_chunk (%s)", coordinates));
                return;
            }

            chunk.setPopulated(true);

            // set the populated chunk back to this world; this also deserializes the chunk
            GlowServer.eventLogger.start(String.format("deserialize_output (%s)", coordinates));
            chunkData.getChunk(chunk);
            GlowServer.eventLogger.stop(String.format("deserialize_output (%s)", coordinates));

            // start pulse tasks
            if (chunkData.pulseTasks != null) {
                for (PopulateInfo.PopulateOutput.PulseTaskInfo pti : chunkData.pulseTasks) {
                    pti.getPulseTask(world).startPulseTask();
                }
            }

            // send block change messages to players
            GlowServer.eventLogger.start(String.format("BCM (%s)", coordinates));
            if (chunkData.changedBlocks != null) {
                for (BlockChangeMessage message : chunkData.changedBlocks) {
                    world.getBlockAt(message.getX(), message.getY(), message.getZ()).setTypeIdAndData(
                            message.getType() >> 4, (byte) (message.getType() & 0xf), true
                    );
                }
            }
            GlowServer.eventLogger.stop(String.format("BCM (%s)", coordinates));

            chunk.setPopulated(true);

            EventFactory.getInstance().callEvent(new ChunkPopulateEvent(chunk));
        } finally {
            // stop serverless chunk population timer
            lock.unlock();
            GlowServer.eventLogger.stop(String.format("sync_chunk (%s)", coordinates));
        }
    }

    /**
     * Force a chunk to be populated by loading the chunks in an area around it. Used when streaming
     * chunks to players so that they do not have to watch chunks being populated.
     *
     * @param coordinates the chunk's coordinates
     */
    public void forcePopulation(IntCoordinates2D coordinates) {
        try {
            populateChunk(coordinates, true);
        } catch (Throwable ex) {
            ConsoleMessages.Error.Chunk.POP_FAILED.log(ex, coordinates);
        }
    }

    /**
     * Initialize a single chunk from the chunk generator.
     */
    private void generateChunk(GlowChunk chunk) {
        int x = chunk.getX();
        int z = chunk.getZ();

        Random random = new Random(x * 341873128712L + z * 132897987541L);
        BiomeGrid biomes = new BiomeGrid();

        int[] biomeValues = biomeGrid[0].generateValues(
                x * GlowChunk.WIDTH,
                z * GlowChunk.HEIGHT,
                GlowChunk.WIDTH,
                GlowChunk.HEIGHT
        );
        for (int i = 0; i < biomeValues.length; i++) {
            biomes.biomes[i] = (byte) biomeValues[i];
        }

        // extended sections with data
        GlowChunkData glowChunkData = null;
        if (generator instanceof GlowChunkGenerator) {
            glowChunkData = (GlowChunkData) generator.generateChunkData(world, random, x, z, biomes);
        } else {
            ChunkGenerator.ChunkData chunkData = generator.generateChunkData(world, random, x, z, biomes);
            if (chunkData != null) {
                glowChunkData = new GlowChunkData(world);
                for (int i = 0; i < 16; ++i) {
                    for (int j = 0; j < 16; ++j) {
                        int maxHeight = chunkData.getMaxHeight();
                        for (int k = 0; k < maxHeight; ++k) {
                            MaterialData materialData = chunkData.getTypeAndData(i, k, j);
                            if (materialData == null) {
                                materialData = new MaterialData(Material.AIR);
                            }
                            glowChunkData.setBlock(i, k, j, materialData);
                        }
                    }
                }
            }
        }

        if (glowChunkData != null) {
            short[][] extSections = glowChunkData.getSections();
            if (extSections != null) {
                ChunkSection[] sections = new ChunkSection[extSections.length];
                for (int i = 0; i < extSections.length; ++i) {
                    if (extSections[i] != null) {
                        sections[i] = ChunkSection.fromStateArray(extSections[i]);
                    }
                }
                chunk.initializeSections(sections);
                chunk.setBiomes(biomes.biomes);
                chunk.automaticHeightMap();
                return;
            }
        }

        // extended sections
        short[][] extSections = generator.generateExtBlockSections(world, random, x, z, biomes);
        if (extSections != null) {
            ChunkSection[] sections = new ChunkSection[extSections.length];
            for (int i = 0; i < extSections.length; ++i) {
                if (extSections[i] != null) {
                    sections[i] = ChunkSection.fromIdArray(extSections[i]);
                }
            }
            chunk.initializeSections(sections);
            chunk.setBiomes(biomes.biomes);
            chunk.automaticHeightMap();
            return;
        }

        // normal sections
        byte[][] blockSections = generator.generateBlockSections(world, random, x, z, biomes);
        if (blockSections != null) {
            ChunkSection[] sections = new ChunkSection[blockSections.length];
            for (int i = 0; i < blockSections.length; ++i) {
                if (blockSections[i] != null) {
                    sections[i] = ChunkSection.fromIdArray(blockSections[i]);
                }
            }
            chunk.initializeSections(sections);
            chunk.setBiomes(biomes.biomes);
            chunk.automaticHeightMap();
            return;
        }

        // deprecated flat generation
        byte[] types = generator.generate(world, random, x, z);
        ChunkSection[] sections = new ChunkSection[8];
        for (int sy = 0; sy < sections.length; ++sy) {
            // We can't use a normal constructor here due to the "interesting"
            // choices used for this deprecated API (blocks are in vertical columns)
            ChunkSection sec = new ChunkSection();
            int by = 16 * sy;
            for (int cx = 0; cx < 16; ++cx) {
                for (int cz = 0; cz < 16; ++cz) {
                    for (int cy = by; cy < by + 16; ++cy) {
                        char type = (char) types[(((cx << 4) + cz) << 7) + cy];
                        sec.setType(cx, cy, cz, (char) (type << 4));
                    }
                }
            }
            sections[sy] = sec;
        }
        chunk.initializeSections(sections);
        chunk.setBiomes(biomes.biomes);
        chunk.automaticHeightMap();
    }

    /**
     * Forces generation of the given chunk.
     *
     * @param coordinates the chunk's coordinates
     * @return Whether the chunk was successfully regenerated.
     */
    public boolean forceRegeneration(IntCoordinates2D coordinates) {
        GlowChunk chunk = getChunk(coordinates);

        if (chunk == null || !chunk.unload(false, false)) {
            return false;
        }

        chunk.setPopulated(false);
        try {
            generateChunk(chunk);
            populateChunk(coordinates, false);  // should this be forced?
        } catch (Throwable ex) {
            ConsoleMessages.Error.Chunk.REGEN_FAILED.log(ex, chunk.getX(), chunk.getZ());
            return false;
        }
        return true;
    }

    /**
     * Gets a list of loaded chunks.
     *
     * @return The currently loaded chunks.
     */
    public GlowChunk[] getLoadedChunks() {
        return getChunks().values().stream()
                .filter(GlowChunk::isLoaded)
                .toArray(GlowChunk[]::new);
    }

    /**
     * Performs the save for the given chunk using the storage provider.
     *
     * @param chunk The chunk to save.
     * @return True if the save was successful.
     */
    public boolean performSave(GlowChunk chunk) {
        if (chunk.isLoaded()) {
            try {
                service.write(chunk);
                return true;
            } catch (IOException ex) {
                ConsoleMessages.Error.Chunk.SAVE_FAILED.log(ex, chunk);
                return false;
            }
        }
        return false;
    }

    public int[] getBiomeGridAtLowerRes(int x, int z, int sizeX, int sizeZ) {
        return biomeGrid[1].generateValues(x, z, sizeX, sizeZ);
    }

    public int[] getBiomeGrid(int x, int z, int sizeX, int sizeZ) {
        return biomeGrid[0].generateValues(x, z, sizeX, sizeZ);
    }

    /**
     * Indicates that a chunk should be locked. A chunk may be locked multiple times, and will only
     * be unloaded when all instances of a lock has been released.
     *
     * @param coordinates the chunk's coordinates
     */
    private void acquireLock(IntCoordinates2D coordinates) {
        lockSet.add(coordinates);
    }

    /**
     * Releases one instance of a chunk lock. A chunk may be locked multiple times, and will only be
     * unloaded when all instances of a lock has been released.
     *
     * @param coordinates the chunk's coordinates
     */
    private void releaseLock(IntCoordinates2D coordinates) {
        lockSet.remove(coordinates);
    }

    /**
     * Prints a visual representation of the terrain state to a temporary file
     * This can be useful to manually check the behavior of terrain generation and loading policies
     * It is up to the user (or the operating system) to remove the file
     * <p>
     * Chunks are represented by ASCI characters
     * Within a line, chunks are sorted on increasing x coordinates
     * Between lines, lower line numbers indicate higher z coordinates (i.e., lines indicate descending z coordinates)
     * <p>
     * Every chunk is represented by two consecutive ASCI characters
     * <p>
     * The first character indicates if the chunk is present in the this.chunks map
     * - If not, the character is ' '
     * - Else if the chunk is populated, the character is 'P'
     * - Else if the chunk is loaded, the character is 'L'
     * - Else the character is '?'
     * <p>
     * The second character indicates if the chunk is currently locked (in lockSet)
     * - If not, the character is ' '
     * - Else if the number of locks held on the chunk is between 0-9, the character is the number of locks
     * (e.g., 7)
     * - Else the character is 'X'
     *
     * @return the file path containing the output
     */
    public Path prettyPrintChunks() throws IOException {
        lock.lock();
        Path tmpFile = Files.createTempFile("", "");
        try (FileWriter fw = new FileWriter(tmpFile.toFile())) {
            StringBuilder map = new StringBuilder();

            int minx = Integer.MAX_VALUE;
            int maxx = Integer.MIN_VALUE;
            int minz = Integer.MAX_VALUE;
            int maxz = Integer.MIN_VALUE;
            for (IntCoordinates2D coordinates : chunks.keySet()) {
                int x = coordinates.getX();
                int z = coordinates.getZ();
                minx = Math.min(minx, x);
                maxx = Math.max(maxx, x);
                minz = Math.min(minz, z);
                maxz = Math.max(maxz, z);
            }
            for (IntCoordinates2D coordinates : lockSet) {
                int x = coordinates.getX();
                int z = coordinates.getZ();
                minx = Math.min(minx, x);
                maxx = Math.max(maxx, x);
                minz = Math.min(minz, z);
                maxz = Math.max(maxz, z);
            }
            for (int z = maxz; z >= minz; z--) {
                for (int x = minx; x <= maxx; x++) {
                    IntCoordinates2D coordinates = new IntCoordinates2D(x, z);
                    if (!chunks.containsKey(coordinates)) {
                        map.append(' ');
                    } else {
                        GlowChunk chunk = chunks.get(coordinates);
                        if (chunk.isPopulated()) {
                            map.append('P');
                        } else if (chunk.isLoaded()) {
                            map.append('L');
                        } else {
                            map.append('?');
                        }
                    }

                    if (!lockSet.contains(coordinates)) {
                        map.append(' ');
                    } else {
                        int count = lockSet.count(coordinates);
                        if (count >= 0 && count < 10) {
                            map.append(count);
                        } else {
                            map.append('X');
                        }
                    }
                }
                map.append("\n");
            }

            fw.write(map.toString());
            return tmpFile;
        } finally {
            lock.unlock();
        }
    }

    /**
     * A set of chunk coordinates that should not be unloaded.
     * Every GlowPlayer instance has a ChunkLock, all linking back to the lockSet field in the ChunkManager.
     */
    public static class ChunkLock implements Iterable<IntCoordinates2D>, Closeable {

        private final ChunkManager cm;
        private final String desc;
        private final Set<IntCoordinates2D> keys = new HashSet<>();
        private final AtomicBoolean isClosed = new AtomicBoolean(false);

        public ChunkLock(ChunkManager cm, String desc) {
            this.cm = cm;
            this.desc = desc;
        }

        /**
         * Acquires a lock on the given chunk key, if it's not already held.
         *
         * @param coordinates the chunk's coordinates
         */
        public synchronized void acquire(IntCoordinates2D coordinates) {
            if (keys.contains(coordinates) || isClosed.get()) {
                return;
            }
            keys.add(coordinates);
            cm.acquireLock(coordinates);
        }

        /**
         * Releases a lock on the given chunk coordinates, if it's not already held.
         *
         * @param coordinates the coordinates to lock
         */
        public synchronized void release(IntCoordinates2D coordinates) {
            if (!keys.contains(coordinates)) {
                return;
            }
            keys.remove(coordinates);
            cm.releaseLock(coordinates);
        }

        /**
         * Release all locks.
         */
        public synchronized void clear() {
            for (IntCoordinates2D coordinates : keys) {
                cm.releaseLock(coordinates);
            }
            keys.clear();
        }

        @Override
        public String toString() {
            return "ChunkLock{" + desc + "}";
        }

        @NotNull
        @Override
        public Iterator<IntCoordinates2D> iterator() {
            return keys.iterator();
        }

        /**
         * Release all locks, and prevent new locks from being acquired (e.g., by other threads).
         */
        @Override
        public synchronized void close() {
            isClosed.set(true);
            clear();
        }
    }

    /**
     * A BiomeGrid implementation for chunk generation.
     */
    private static class BiomeGrid implements ChunkGenerator.BiomeGrid {

        private final byte[] biomes = new byte[256];

        @Override
        public Biome getBiome(int x, int z) {
            // upcasting is very important to get extended biomes
            return GlowBiome.getBiome(biomes[x | z << 4] & 0xFF);
        }

        @Override
        public void setBiome(int x, int z, Biome bio) {
            biomes[x | z << 4] = (byte) GlowBiome.getId(bio);
        }
    }

    public ConcurrentMap<IntCoordinates2D, GlowChunk> getChunks() {
        return chunks;
    }
}
