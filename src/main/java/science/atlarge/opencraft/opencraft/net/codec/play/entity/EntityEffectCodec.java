package science.atlarge.opencraft.opencraft.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityEffectMessage;

import java.io.IOException;

public final class EntityEffectCodec implements Codec<EntityEffectMessage> {

    @Override
    public EntityEffectMessage decode(ByteBuf buffer) throws IOException {
        int id = ByteBufUtils.readVarInt(buffer);
        byte effect = buffer.readByte();
        byte amplifier = buffer.readByte();
        int duration = ByteBufUtils.readVarInt(buffer);
        boolean hideParticles = buffer.readBoolean();
        return new EntityEffectMessage(id, effect, amplifier, duration, hideParticles);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, EntityEffectMessage message) {
        ByteBufUtils.writeVarInt(buffer, message.getId());
        buffer.writeByte(message.getEffect());
        buffer.writeByte(message.getAmplifier());
        ByteBufUtils.writeVarInt(buffer, message.getDuration());
        buffer.writeBoolean(message.isHideParticles());
        return buffer;
    }
}
