package science.atlarge.opencraft.opencraft.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import science.atlarge.opencraft.opencraft.entity.meta.MetadataMap.Entry;

import java.util.List;
import java.util.UUID;

@Data
public final class SpawnPlayerMessage implements Message {

    private final int id;
    private final UUID uuid;
    private final double x;
    private final double y;
    private final double z;
    private final int rotation;
    private final int pitch;
    private final List<Entry> metadata;

}
