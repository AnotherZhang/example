package science.atlarge.opencraft.opencraft.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import science.atlarge.opencraft.opencraft.net.message.play.game.ExperienceMessage;

import java.io.IOException;

public final class ExperienceCodec implements Codec<ExperienceMessage> {

    @Override
    public ExperienceMessage decode(ByteBuf buffer) throws IOException {
        float barValue = buffer.readFloat();
        int level = ByteBufUtils.readVarInt(buffer);
        int totalExp = ByteBufUtils.readVarInt(buffer);
        return new ExperienceMessage(barValue, level, totalExp);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, ExperienceMessage message) {
        buffer.writeFloat(message.getBarValue());
        ByteBufUtils.writeVarInt(buffer, message.getLevel());
        ByteBufUtils.writeVarInt(buffer, message.getTotalExp());
        return buffer;
    }
}
