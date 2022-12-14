package science.atlarge.opencraft.opencraft.io.structure;

import lombok.Data;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NonNls;
import science.atlarge.opencraft.opencraft.generator.structures.GlowStructurePiece;
import science.atlarge.opencraft.opencraft.generator.structures.util.StructureBoundingBox;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

/**
 * The base for structure piece store classes.
 *
 * @param <T> The type of structure piece being stored.
 */
@Data
public abstract class StructurePieceStore<T extends GlowStructurePiece> {

    private final Class<T> type;
    @NonNls
    private final String id;

    /**
     * Create a structure piece of this store's type.
     *
     * <p>The load method will be called separately.
     *
     * @return The structure piece.
     */
    public abstract T createStructurePiece();

    /**
     * Load structure piece data of the appropriate type from the given compound tag.
     *
     * @param structurePiece The target structure piece.
     * @param compound       The structure piece's tag.
     */
    public void load(T structurePiece, CompoundTag compound) {
        compound.readInt("GD", structurePiece::setUnknownGd);
        compound.readInt("O", structurePiece::setNumericOrientation);
        compound.readIntArray("BB", bb -> {
            if (bb.length == 6) {
                StructureBoundingBox boundingBox = new StructureBoundingBox(
                        new Vector(bb[0], bb[1], bb[2]), new Vector(bb[3], bb[4], bb[5]));
                structurePiece.setBoundingBox(boundingBox);
            }
        });
    }

    /**
     * Save information about this structure piece to the given tag.
     *
     * @param structurePiece The structure piece to save.
     * @param compound       The target tag.
     */
    public void save(T structurePiece, CompoundTag compound) {
        compound.putInt("GD", structurePiece.getUnknownGd());
        compound.putInt("O", structurePiece.getNumericOrientation());
        StructureBoundingBox boundingBox = structurePiece.getBoundingBox();
        int[] bb = new int[6];
        bb[0] = boundingBox.getMin().getBlockX();
        bb[1] = boundingBox.getMin().getBlockY();
        bb[2] = boundingBox.getMin().getBlockZ();
        bb[3] = boundingBox.getMax().getBlockX();
        bb[4] = boundingBox.getMax().getBlockY();
        bb[5] = boundingBox.getMax().getBlockZ();
        compound.putIntArray("BB", bb);
    }
}
