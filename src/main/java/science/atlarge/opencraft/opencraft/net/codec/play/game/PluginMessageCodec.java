package science.atlarge.opencraft.opencraft.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import science.atlarge.opencraft.opencraft.net.message.play.game.PluginMessage;

import java.io.IOException;

public final class PluginMessageCodec implements Codec<PluginMessage> {

    @Override
    public PluginMessage decode(ByteBuf buffer) throws IOException {
        String channel = ByteBufUtils.readUTF8(buffer);
        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
        return new PluginMessage(channel, data);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, PluginMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buffer, message.getChannel());
        buffer.writeBytes(message.getData());
        return buffer;
    }
}
