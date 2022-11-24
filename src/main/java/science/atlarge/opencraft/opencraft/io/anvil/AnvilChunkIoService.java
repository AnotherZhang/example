package science.atlarge.opencraft.opencraft.io.anvil;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.ItemTable;
import science.atlarge.opencraft.opencraft.block.blocktype.BlockType;
import science.atlarge.opencraft.opencraft.block.entity.BlockEntity;
import science.atlarge.opencraft.opencraft.chunk.ChunkSection;
import science.atlarge.opencraft.opencraft.chunk.GlowChunk;
import science.atlarge.opencraft.opencraft.chunk.GlowChunkSnapshot;
import science.atlarge.opencraft.opencraft.constants.ItemIds;
import science.atlarge.opencraft.opencraft.entity.GlowEntity;
import science.atlarge.opencraft.opencraft.i18n.ConsoleMessages;
import science.atlarge.opencraft.opencraft.io.ChunkIoService;
import science.atlarge.opencraft.opencraft.io.entity.EntityStorage;
import science.atlarge.opencraft.opencraft.io.entity.UnknownEntityTypeException;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;
import science.atlarge.opencraft.opencraft.util.nbt.NbtInputStream;
import science.atlarge.opencraft.opencraft.util.nbt.NbtOutputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * An implementation of the {@link ChunkIoService} which reads and writes Anvil maps, an improvement
 * on the McRegion file format.
 */
public final class AnvilChunkIoService implements ChunkIoService {

    /**
     * The size of a region - a 32x32 group of chunks.
     */
    private static final int REGION_SIZE = 32;

    /**
     * The region file cache.
     */
    private final RegionFileCache cache;

    // todo: consider the session.lock file

    public AnvilChunkIoService(File dir) {
        cache = new RegionFileCache(dir, ".mca"); // NON-NLS
    }

    public static boolean read(GlowChunk chunk, String b64ChunkData) throws IOException {
        // trim the input because it's of form {"ChunkData":"..."}
        byte[] chunkBytes = Base64.getDecoder().decode(b64ChunkData.substring(14, b64ChunkData.length() - 2));
        ByteArrayInputStream inputStreamBuffer = new ByteArrayInputStream(chunkBytes);
        DataInputStream inputStream = new DataInputStream(inputStreamBuffer);
        return AnvilChunkIoService.read(chunk, inputStream);
    }

    @Override
    public boolean read(GlowChunk chunk) throws IOException {
        int x = chunk.getX();
        int z = chunk.getZ();
        RegionFile region = cache.getRegionFile(x, z);
        int regionX = x & REGION_SIZE - 1;
        int regionZ = z & REGION_SIZE - 1;
        if (!region.hasChunk(regionX, regionZ)) {
            return false;
        }

        DataInputStream in = region.getChunkDataInputStream(regionX, regionZ);
        return read(chunk, in);
    }

    public static boolean read(GlowChunk chunk, DataInputStream in) throws IOException {
        CompoundTag levelTag;
        try (NbtInputStream nbt = new NbtInputStream(in, false)) {
            CompoundTag root = nbt.readCompound();
            levelTag = root.getCompound("Level"); // NON-NLS
        }

        // backup current sections and unload the chunk, in order to be able to reinitialize its sections
        ChunkSection[] prevSections = chunk.getSections();
        chunk.unload(false, false);

        // read the vertical sections
        List<CompoundTag> sectionList = levelTag.getCompoundList("Sections"); // NON-NLS
        ChunkSection[] sections = new ChunkSection[GlowChunk.SEC_COUNT];
        for (CompoundTag sectionTag : sectionList) {
            int y = sectionTag.getByte("Y"); // NON-NLS
            if (y < 0 || y > GlowChunk.SEC_COUNT) {
                ConsoleMessages.Warn.Chunk.SECTION_OOB.log(y, chunk);
                continue;
            }

            if (sections[y] != null) {
                ConsoleMessages.Warn.Chunk.SECTION_DUP.log(y, chunk);
                continue;
            }

            sections[y] = ChunkSection.fromNbt(sectionTag);

            // if the chunk already had sections, we try to combine them with the stream data
            if (prevSections != null && prevSections[y] != null) {
                sections[y].combineTypes(prevSections[y].getTypes());
            }
        }

        // initialize the chunk
        if (!chunk.isLoaded()) {
            chunk.initializeSections(sections);
        }
        chunk.setPopulated(levelTag.getBoolean("TerrainPopulated", false)); // NON-NLS
        levelTag.readLong("InhabitedTime", chunk::setInhabitedTime);

        // read biomes
        levelTag.readByteArray("Biomes", chunk::setBiomes); // NON-NLS
        // read height map
        if (!levelTag.readIntArray("HeightMap", chunk::setHeightMap)) { // NON-NLS
            chunk.automaticHeightMap();
        }

        // read slime chunk
        levelTag.readByte("isSlimeChunk", chunk::setIsSlimeChunk); // NON-NLS

        // read entities
        levelTag.iterateCompoundList("Entities", entityTag -> { // NON-NLS
            try {
                // note that creating the entity is sufficient to add it to the world
                EntityStorage.loadEntity(chunk.getWorld(), entityTag);
            } catch (UnknownEntityTypeException e) {
                ConsoleMessages.Warn.Entity.UNKNOWN.log(chunk, e.getIdOrTag());
            } catch (Exception e) {
                ConsoleMessages.Warn.Entity.LOAD_FAILED.log(e, chunk);
            }
        });

        // read block entities
        List<CompoundTag> storedBlockEntities = levelTag.getCompoundList("TileEntities"); // NON-NLS
        BlockEntity blockEntity;
        for (CompoundTag blockEntityTag : storedBlockEntities) {
            int tx = blockEntityTag.getInt("x"); // NON-NLS
            int ty = blockEntityTag.getInt("y"); // NON-NLS
            int tz = blockEntityTag.getInt("z"); // NON-NLS
            blockEntity = chunk
                    .createEntity(tx & 0xf, ty, tz & 0xf, chunk.getType(tx & 0xf, tz & 0xf, ty));
            if (blockEntity != null) {
                try {
                    blockEntity.loadNbt(blockEntityTag);
                } catch (Exception ex) {
                    String id = blockEntityTag.tryGetString("id").orElse("<missing>"); // NON-NLS
                    ConsoleMessages.Error.BlockEntity.LOAD_FAILED.log(
                            ex, blockEntity.getBlock(), id);
                }
            } else {
                String id =
                        blockEntityTag.tryGetString("id").orElse("<missing>"); // NON-NLS
                ConsoleMessages.Warn.BlockEntity.UNKNOWN.log(
                        chunk.getWorld().getName(), tx, ty, tz, id);
            }
        }

        levelTag.iterateCompoundList("TileTicks", tileTick -> { // NON-NLS
            int tileX = tileTick.getInt("x"); // NON-NLS
            int tileY = tileTick.getInt("y"); // NON-NLS
            int tileZ = tileTick.getInt("z"); // NON-NLS
            String id = tileTick.getString("i"); // NON-NLS
            Material material = ItemIds.getBlock(id);
            if (material == null) {
                ConsoleMessages.Warn.Chunk.UNKNOWN_BLOCK_TO_TICK.log(id);
                return;
            }
            GlowBlock block = chunk.getBlock(tileX, tileY, tileZ);
            if (material != block.getType()) {
                return;
            }
            // TODO tick delay: tileTick.getInt("t");
            // TODO ordering: tileTick.getInt("p");
            BlockType type = ItemTable.instance().getBlock(material);
            if (type == null) {
                return;
            }
            block.getWorld().requestPulse(block);
        });

        return true;
    }

    @Override
    public void write(GlowChunk chunk) throws IOException {
        int x = chunk.getX();
        int z = chunk.getZ();
        int regionX = x & REGION_SIZE - 1;
        int regionZ = z & REGION_SIZE - 1;
        RegionFile region = cache.getRegionFile(x, z);

        write(chunk, region.getChunkDataOutputStream(regionX, regionZ));
    }

    public static void write(GlowChunk chunk, DataOutputStream outputStream) throws IOException {
        CompoundTag levelTags = new CompoundTag();

        // core properties
        levelTags.putInt("xPos", chunk.getX()); // NON-NLS
        levelTags.putInt("zPos", chunk.getZ()); // NON-NLS
        levelTags.putLong("LastUpdate", 0); // NON-NLS
        levelTags.putLong("InhabitedTime", chunk.getInhabitedTime()); // NON-NLS
        levelTags.putBool("TerrainPopulated", chunk.isPopulated()); // NON-NLS

        // chunk sections
        List<CompoundTag> sectionTags = new ArrayList<>();
        GlowChunkSnapshot snapshot = chunk.getChunkSnapshot(true, true, false);
        ChunkSection[] sections = snapshot.getRawSections();
        for (byte i = 0; i < sections.length; ++i) {
            ChunkSection sec = sections[i];
            if (sec == null) {
                continue;
            }

            CompoundTag sectionTag = new CompoundTag();
            sectionTag.putByte("Y", i); // NON-NLS
            sec.optimize();
            sec.writeToNbt(sectionTag);
            sectionTags.add(sectionTag);
        }
        levelTags.putCompoundList("Sections", sectionTags); // NON-NLS

        // height map and biomes
        levelTags.putIntArray("HeightMap", snapshot.getRawHeightmap()); // NON-NLS
        levelTags.putByteArray("Biomes", snapshot.getRawBiomes()); // NON-NLS

        // Save Slime Chunk
        levelTags.putByte("isSlimeChunk", snapshot.isSlimeChunk() ? 1 : 0); // NON-NLS

        // entities
        List<CompoundTag> entities = new ArrayList<>();
        for (GlowEntity entity : chunk.getRawEntities()) {
            if (!entity.shouldSave()) {
                continue;
            }
            // passengers will be saved as part of the vehicle
            if (entity.isInsideVehicle()) {
                continue;
            }
            try {
                CompoundTag tag = new CompoundTag();
                EntityStorage.save(entity, tag);
                entities.add(tag);
            } catch (Exception e) {
                ConsoleMessages.Warn.Entity.SAVE_FAILED.log(e, entity, chunk);
            }
        }
        levelTags.putCompoundList("Entities", entities);

        // block entities
        List<CompoundTag> blockEntities = new ArrayList<>();
        for (BlockEntity entity : chunk.getRawBlockEntities()) {
            try {
                CompoundTag tag = new CompoundTag();
                entity.saveNbt(tag);
                blockEntities.add(tag);
            } catch (Exception ex) {
                ConsoleMessages.Error.BlockEntity.SAVE_FAILED.log(ex, entity.getBlock());
            }
        }
        levelTags.putCompoundList("TileEntities", blockEntities);

        List<CompoundTag> tileTicks = new ArrayList<>();
        if (chunk.getWorld().getTickMap() != null) {
            for (Location location : chunk.getWorld().getTickMap()) {
                Chunk locationChunk = location.getChunk();
                if (locationChunk.getX() == chunk.getX() && locationChunk.getZ() == chunk.getZ()) {
                    int tileX = location.getBlockX();
                    int tileY = location.getBlockY();
                    int tileZ = location.getBlockZ();
                    String type = ItemIds.getName(location.getBlock().getType());
                    CompoundTag tag = new CompoundTag();
                    tag.putInt("x", tileX);
                    tag.putInt("y", tileY);
                    tag.putInt("z", tileZ);
                    tag.putString("i", type);
                    tileTicks.add(tag);
                }
            }
        }
        levelTags.putCompoundList("TileTicks", tileTicks);

        CompoundTag levelOut = new CompoundTag();
        levelOut.putCompound("Level", levelTags);

        try (NbtOutputStream nbt = new NbtOutputStream(outputStream, false)) {
            nbt.writeTag(levelOut);
        }
    }

    @Override
    public void unload() throws IOException {
        cache.clear();
    }

}
