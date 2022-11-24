package science.atlarge.opencraft.opencraft.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import org.bukkit.util.BlockVector;
import science.atlarge.opencraft.opencraft.net.GlowBufUtils;
import science.atlarge.opencraft.opencraft.net.message.play.player.UseBedMessage;

import java.io.IOException;

public final class UseBedCodec implements Codec<UseBedMessage> {

    @Override
    public UseBedMessage decode(ByteBuf buffer) throws IOException {
        int id = ByteBufUtils.readVarInt(buffer);
        BlockVector pos = GlowBufUtils.readBlockPosition(buffer);
        return new UseBedMessage(id, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, UseBedMessage message) {
        ByteBufUtils.writeVarInt(buffer, message.getId());
        GlowBufUtils.writeBlockPosition(buffer, message.getX(), message.getY(), message.getZ());
        return buffer;
    }
}
