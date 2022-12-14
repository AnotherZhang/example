package science.atlarge.opencraft.opencraft.advancement;

import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.net.GlowBufUtils;
import science.atlarge.opencraft.opencraft.util.TextMessage;

import java.io.IOException;


@Data
public class GlowAdvancementDisplay {

    private final TextMessage title;
    private final TextMessage description;
    private final ItemStack icon;
    private final FrameType type;
    private final float x;
    private final float y;

    /**
     * Writes this notification to the given {@link ByteBuf}.
     *
     * @param buf the buffer to write to
     * @return {@code buf}, with this notification written to it
     * @throws IOException if a string is too long
     */
    public ByteBuf encode(ByteBuf buf) throws IOException {
        GlowBufUtils.writeChat(buf, title);
        GlowBufUtils.writeChat(buf, description);
        GlowBufUtils.writeSlot(buf, icon);
        ByteBufUtils.writeVarInt(buf, type.ordinal());
        buf.writeInt((1 << 0x4)); // todo: flags
        buf.writeFloat(x);
        buf.writeFloat(y);
        return buf;
    }

    public enum FrameType {
        TASK, CHALLENGE, GOAL
    }
}
