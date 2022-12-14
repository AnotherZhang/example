package science.atlarge.opencraft.opencraft.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import org.bukkit.util.BlockVector;
import science.atlarge.opencraft.opencraft.net.GlowBufUtils;
import science.atlarge.opencraft.opencraft.net.message.play.player.DiggingMessage;

public final class DiggingCodec implements Codec<DiggingMessage> {

    @Override
    public DiggingMessage decode(ByteBuf buffer) {
        int state = buffer.readByte();
        BlockVector pos = GlowBufUtils.readBlockPosition(buffer);
        int face = buffer.readByte();
        return new DiggingMessage(state, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), face);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, DiggingMessage message) {
        buffer.writeByte(message.getState());
        GlowBufUtils.writeBlockPosition(buffer, message.getX(), message.getY(), message.getZ());
        buffer.writeByte(message.getFace());
        return buffer;
    }
}
