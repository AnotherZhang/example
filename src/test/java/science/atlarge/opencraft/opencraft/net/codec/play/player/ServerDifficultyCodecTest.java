package science.atlarge.opencraft.opencraft.net.codec.play.player;

import com.flowpowered.network.Codec;
import org.bukkit.Difficulty;
import science.atlarge.opencraft.opencraft.net.codec.CodecTest;
import science.atlarge.opencraft.opencraft.net.message.play.player.ServerDifficultyMessage;

public class ServerDifficultyCodecTest extends CodecTest<ServerDifficultyMessage> {

    @Override
    protected Codec<ServerDifficultyMessage> createCodec() {
        return new ServerDifficultyCodec();
    }

    @Override
    protected ServerDifficultyMessage createMessage() {
        return new ServerDifficultyMessage(Difficulty.NORMAL);
    }
}
