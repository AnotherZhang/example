package science.atlarge.opencraft.opencraft.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

import java.util.UUID;

@Data
public final class SpectateMessage implements Message {

    private final UUID target;

}

