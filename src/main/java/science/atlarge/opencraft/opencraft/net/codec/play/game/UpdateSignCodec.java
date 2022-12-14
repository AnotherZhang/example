package science.atlarge.opencraft.opencraft.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import org.bukkit.util.BlockVector;
import science.atlarge.opencraft.opencraft.net.GlowBufUtils;
import science.atlarge.opencraft.opencraft.net.message.play.game.UpdateSignMessage;
import science.atlarge.opencraft.opencraft.util.TextMessage;

import java.io.IOException;

public final class UpdateSignCodec implements Codec<UpdateSignMessage> {

    @Override
    public UpdateSignMessage decode(ByteBuf buffer) throws IOException {
        BlockVector pos = GlowBufUtils.readBlockPosition(buffer);
        TextMessage[] message = new TextMessage[4];
        for (int i = 0; i < message.length; ++i) {
            message[i] = GlowBufUtils.readChat(buffer);
        }
        return new UpdateSignMessage(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), message);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, UpdateSignMessage message) throws IOException {
        GlowBufUtils.writeBlockPosition(buffer, message.getX(), message.getY(), message.getZ());
        for (TextMessage line : message.getMessage()) {
            GlowBufUtils.writeChat(buffer, line);
        }
        return buffer;
    }
}
