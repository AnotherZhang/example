package science.atlarge.opencraft.opencraft.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import science.atlarge.opencraft.opencraft.util.Position;

@Data
@RequiredArgsConstructor
public final class EntityTeleportMessage implements Message {

    private final int id;
    private final double x;
    private final double y;
    private final double z;
    private final int rotation;
    private final int pitch;
    private final boolean onGround;

    public EntityTeleportMessage(int id, double x, double y, double z, int rotation, int pitch) {
        this(id, x, y, z, rotation, pitch, true);
    }

    public EntityTeleportMessage(int id, Location location) {
        this(id, location.getX(), location.getY(), location.getZ(), Position.getIntYaw(location),
                Position.getIntPitch(location));
    }
}
