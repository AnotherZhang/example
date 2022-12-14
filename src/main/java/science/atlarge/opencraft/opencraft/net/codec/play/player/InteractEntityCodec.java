package science.atlarge.opencraft.opencraft.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import science.atlarge.opencraft.opencraft.net.message.play.player.InteractEntityMessage;

import java.io.IOException;

public final class InteractEntityCodec implements Codec<InteractEntityMessage> {

    @Override
    public InteractEntityMessage decode(ByteBuf buffer) throws IOException {
        int id = ByteBufUtils.readVarInt(buffer);
        int action = ByteBufUtils.readVarInt(buffer);
        if (action == InteractEntityMessage.Action.INTERACT_AT.ordinal()) {
            float targetX = buffer.readFloat();
            float targetY = buffer.readFloat();
            float targetZ = buffer.readFloat();
            int hand = ByteBufUtils.readVarInt(buffer);
            return new InteractEntityMessage(id, action, targetX, targetY, targetZ, hand);
        } else if (action == InteractEntityMessage.Action.INTERACT.ordinal()) {
            int hand = ByteBufUtils.readVarInt(buffer);
            return new InteractEntityMessage(id, action, hand);
        }
        return new InteractEntityMessage(id, action);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, InteractEntityMessage message) {
        ByteBufUtils.writeVarInt(buffer, message.getId());
        ByteBufUtils.writeVarInt(buffer, message.getAction());
        if (message.getAction() == InteractEntityMessage.Action.INTERACT_AT.ordinal()) {
            buffer.writeFloat(message.getTargetX());
            buffer.writeFloat(message.getTargetY());
            buffer.writeFloat(message.getTargetZ());
            ByteBufUtils.writeVarInt(buffer, message.getHand());
        } else if (message.getAction() == InteractEntityMessage.Action.INTERACT.ordinal()) {
            ByteBufUtils.writeVarInt(buffer, message.getHand());
        }
        return buffer;
    }
}
