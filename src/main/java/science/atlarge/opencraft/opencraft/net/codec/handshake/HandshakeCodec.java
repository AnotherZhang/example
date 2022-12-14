package science.atlarge.opencraft.opencraft.net.codec.handshake;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import science.atlarge.opencraft.opencraft.net.message.handshake.HandshakeMessage;

import java.io.IOException;

public final class HandshakeCodec implements Codec<HandshakeMessage> {

    @Override
    public HandshakeMessage decode(ByteBuf buffer) throws IOException {
        int version = ByteBufUtils.readVarInt(buffer);
        String address = ByteBufUtils.readUTF8(buffer);
        int port = buffer.readUnsignedShort();
        int state = ByteBufUtils.readVarInt(buffer);
        return new HandshakeMessage(version, address, port, state);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, HandshakeMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buffer, message.getVersion());
        ByteBufUtils.writeUTF8(buffer, message.getAddress());
        buffer.writeShort(message.getPort());
        ByteBufUtils.writeVarInt(buffer, message.getState());
        return buffer;
    }
}
