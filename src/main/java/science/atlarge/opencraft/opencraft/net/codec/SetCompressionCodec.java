package science.atlarge.opencraft.opencraft.net.codec;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import science.atlarge.opencraft.opencraft.net.message.SetCompressionMessage;

import java.io.IOException;

public final class SetCompressionCodec implements Codec<SetCompressionMessage> {

    @Override
    public SetCompressionMessage decode(ByteBuf buffer) throws IOException {
        int threshold = ByteBufUtils.readVarInt(buffer);
        return new SetCompressionMessage(threshold);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, SetCompressionMessage message) {
        ByteBufUtils.writeVarInt(buffer, message.getThreshold());
        return buffer;
    }
}
