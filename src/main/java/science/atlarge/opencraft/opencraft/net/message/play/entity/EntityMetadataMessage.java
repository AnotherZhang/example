package science.atlarge.opencraft.opencraft.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import science.atlarge.opencraft.opencraft.entity.meta.MetadataMap.Entry;

import java.util.List;

@Data
public final class EntityMetadataMessage implements Message {

    private final int id;
    private final List<Entry> entries;

}
