package science.atlarge.opencraft.opencraft.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import science.atlarge.opencraft.opencraft.net.message.play.entity.SetPassengerMessage;

import java.io.IOException;

public class SetPassengerCodec implements Codec<SetPassengerMessage> {

    @Override
    public SetPassengerMessage decode(ByteBuf buffer) throws IOException {
        int entityId = ByteBufUtils.readVarInt(buffer);
        int length = ByteBufUtils.readVarInt(buffer);
        int[] passengers = new int[length];
        for (int i = 0; i < length; i++) {
            passengers[i] = ByteBufUtils.readVarInt(buffer);
        }
        return new SetPassengerMessage(entityId, passengers);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, SetPassengerMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buffer, message.getEntityId());
        ByteBufUtils.writeVarInt(buffer, message.getPassengers().length);
        for (int passenger : message.getPassengers()) {
            ByteBufUtils.writeVarInt(buffer, passenger);
        }
        return buffer;
    }
}
