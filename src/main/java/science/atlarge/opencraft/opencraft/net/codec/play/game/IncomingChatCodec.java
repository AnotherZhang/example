package science.atlarge.opencraft.opencraft.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import science.atlarge.opencraft.opencraft.net.message.play.game.IncomingChatMessage;

import java.io.IOException;

public final class IncomingChatCodec implements Codec<IncomingChatMessage> {

    @Override
    public IncomingChatMessage decode(ByteBuf buffer) throws IOException {
        return new IncomingChatMessage(ByteBufUtils.readUTF8(buffer));
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, IncomingChatMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buffer, message.getText());
        return buffer;
    }
}
