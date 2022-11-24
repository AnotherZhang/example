package science.atlarge.opencraft.opencraft.net.codec.play.game;

import com.flowpowered.network.Codec;
import science.atlarge.opencraft.opencraft.net.codec.CodecTest;
import science.atlarge.opencraft.opencraft.net.message.play.game.StatisticMessage;

import java.util.HashMap;
import java.util.Map;

public class StatisticCodecTest extends CodecTest<StatisticMessage> {

    @Override
    protected Codec<StatisticMessage> createCodec() {
        return new StatisticCodec();
    }

    @Override
    protected StatisticMessage createMessage() {
        Map<String, Integer> values = new HashMap<>();
        values.put("one", 1);
        return new StatisticMessage(values);
    }
}
