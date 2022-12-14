package science.atlarge.opencraft.opencraft.net.codec.play.game;

import com.flowpowered.network.Codec;
import science.atlarge.opencraft.opencraft.net.codec.CodecTest;
import science.atlarge.opencraft.opencraft.net.message.play.game.ExplosionMessage;

import java.util.ArrayList;
import java.util.Collection;

public class ExplosionCodecTest extends CodecTest<ExplosionMessage> {

    @Override
    protected Codec<ExplosionMessage> createCodec() {
        return new ExplosionCodec();
    }

    @Override
    protected ExplosionMessage createMessage() {
        Collection<ExplosionMessage.Record> records = new ArrayList<>();
        records.add(new ExplosionMessage.Record((byte) 1, (byte) 2, (byte) 3));
        return new ExplosionMessage(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, records);
    }
}
