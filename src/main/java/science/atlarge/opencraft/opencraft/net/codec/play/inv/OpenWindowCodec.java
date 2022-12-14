package science.atlarge.opencraft.opencraft.net.codec.play.inv;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import science.atlarge.opencraft.opencraft.net.GlowBufUtils;
import science.atlarge.opencraft.opencraft.net.message.play.inv.OpenWindowMessage;
import science.atlarge.opencraft.opencraft.util.TextMessage;

import java.io.IOException;

public final class OpenWindowCodec implements Codec<OpenWindowMessage> {

    @Override
    public OpenWindowMessage decode(ByteBuf buffer) throws IOException {
        byte id = buffer.readByte();
        String type = ByteBufUtils.readUTF8(buffer);
        TextMessage title = GlowBufUtils.readChat(buffer);
        byte slots = buffer.readByte();
        int entityId = 0;
        if (buffer.readableBytes() > 0) {
            entityId = buffer.getInt(buffer.readerIndex());
        }
        return new OpenWindowMessage(id, type, title, slots, entityId);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, OpenWindowMessage message) throws IOException {
        buffer.writeByte(message.getId());
        ByteBufUtils.writeUTF8(buffer, message.getType());
        GlowBufUtils.writeChat(buffer, message.getTitle());
        buffer.writeByte(message.getSlots());
        if (message.getEntityId() != 0) {
            buffer.writeInt(message.getEntityId());
        }
        return buffer;
    }
}
