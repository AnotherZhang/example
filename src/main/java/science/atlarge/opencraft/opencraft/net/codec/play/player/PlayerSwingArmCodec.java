package science.atlarge.opencraft.opencraft.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import science.atlarge.opencraft.opencraft.net.message.play.player.PlayerSwingArmMessage;

import java.io.IOException;

public final class PlayerSwingArmCodec implements Codec<PlayerSwingArmMessage> {

    @Override
    public PlayerSwingArmMessage decode(ByteBuf buffer) throws IOException {
        int hand = ByteBufUtils.readVarInt(buffer);
        return new PlayerSwingArmMessage(hand);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, PlayerSwingArmMessage message) {
        ByteBufUtils.writeVarInt(buffer, message.getHand());
        return buffer;
    }
}
