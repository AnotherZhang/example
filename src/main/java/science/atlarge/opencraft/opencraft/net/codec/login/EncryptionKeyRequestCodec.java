package science.atlarge.opencraft.opencraft.net.codec.login;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import science.atlarge.opencraft.opencraft.net.message.login.EncryptionKeyRequestMessage;

import java.io.IOException;

public final class EncryptionKeyRequestCodec implements Codec<EncryptionKeyRequestMessage> {

    @Override
    public EncryptionKeyRequestMessage decode(ByteBuf buffer) throws IOException {

        String sessionId = ByteBufUtils.readUTF8(buffer);

        byte[] publicKey = new byte[ByteBufUtils.readVarInt(buffer)];
        buffer.readBytes(publicKey);

        byte[] verifyToken = new byte[ByteBufUtils.readVarInt(buffer)];
        buffer.readBytes(verifyToken);

        return new EncryptionKeyRequestMessage(sessionId, publicKey, verifyToken);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, EncryptionKeyRequestMessage message) throws IOException {

        ByteBufUtils.writeUTF8(buffer, message.getSessionId());

        ByteBufUtils.writeVarInt(buffer, message.getPublicKey().length);
        buffer.writeBytes(message.getPublicKey());

        ByteBufUtils.writeVarInt(buffer, message.getVerifyToken().length);
        buffer.writeBytes(message.getVerifyToken());

        return buffer;
    }
}
