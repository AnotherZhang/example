package science.atlarge.opencraft.opencraft.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import org.bukkit.Difficulty;
import science.atlarge.opencraft.opencraft.net.message.play.player.ServerDifficultyMessage;

public final class ServerDifficultyCodec implements Codec<ServerDifficultyMessage> {

    @Override
    public ServerDifficultyMessage decode(ByteBuf buffer) {
        int difficulty = buffer.readUnsignedByte();
        return new ServerDifficultyMessage(Difficulty.values()[difficulty]);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, ServerDifficultyMessage message) {
        return buffer.writeByte(message.getDifficulty().ordinal());
    }
}
