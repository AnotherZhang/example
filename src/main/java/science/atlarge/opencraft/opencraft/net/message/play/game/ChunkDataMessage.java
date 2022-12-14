package science.atlarge.opencraft.opencraft.net.message.play.game;

import com.flowpowered.network.Message;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

import java.util.Collection;

@Data
public final class ChunkDataMessage implements Message {

    private final int x;
    private final int z;
    private final boolean continuous;
    private final int primaryMask;
    private final ByteBuf data;
    private final Collection<CompoundTag> blockEntities;
}
