package science.atlarge.opencraft.opencraft.net.codec.play.player;

import com.flowpowered.network.Codec;
import science.atlarge.opencraft.opencraft.net.codec.CodecTest;
import science.atlarge.opencraft.opencraft.net.message.play.player.BossBarMessage;

import java.util.UUID;

public class BossBarCodecTest extends CodecTest<BossBarMessage> {

    @Override
    protected Codec<BossBarMessage> createCodec() {
        return new BossBarCodec();
    }

    @Override
    protected BossBarMessage createMessage() {
        return new BossBarMessage(UUID.randomUUID(), BossBarMessage.Action.UPDATE_HEALTH, 1.0f);
    }
}
