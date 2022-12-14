package science.atlarge.opencraft.opencraft.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityVelocityMessage;

import java.io.IOException;

public final class EntityVelocityCodec implements Codec<EntityVelocityMessage> {

    @Override
    public EntityVelocityMessage decode(ByteBuf buffer) throws IOException {
        int id = ByteBufUtils.readVarInt(buffer);
        int velocityX = buffer.readShort();
        int velocityY = buffer.readShort();
        int velocityZ = buffer.readShort();
        return new EntityVelocityMessage(id, velocityX, velocityY, velocityZ);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, EntityVelocityMessage message) {
        ByteBufUtils.writeVarInt(buffer, message.getId());
        buffer.writeShort(message.getVelocityX());
        buffer.writeShort(message.getVelocityY());
        buffer.writeShort(message.getVelocityZ());
        return buffer;
    }
}
