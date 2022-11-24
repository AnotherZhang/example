package science.atlarge.opencraft.opencraft.net.codec.play.player;

import com.flowpowered.network.Codec;
import science.atlarge.opencraft.opencraft.net.codec.CodecTest;
import science.atlarge.opencraft.opencraft.net.message.play.player.SpectateMessage;

import java.util.UUID;

public class SpectateCodecTest extends CodecTest<SpectateMessage> {

    @Override
    protected Codec<SpectateMessage> createCodec() {
        return new SpectateCodec();
    }

    @Override
    protected SpectateMessage createMessage() {
        return new SpectateMessage(UUID.randomUUID());
    }
}
