package science.atlarge.opencraft.opencraft.block.entity;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Note;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.block.entity.state.GlowNoteBlock;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

public class NoteblockEntity extends BlockEntity {

    @Getter
    @Setter
    private Note note = new Note(0);

    public NoteblockEntity(GlowBlock block) {
        super(block);
        setSaveId("minecraft:noteblock");
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        note = new Note(tag.getByte("note"));
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putByte("note", note.getId());
    }

    @Override
    public GlowBlockState getState() {
        return new GlowNoteBlock(block);
    }
}
