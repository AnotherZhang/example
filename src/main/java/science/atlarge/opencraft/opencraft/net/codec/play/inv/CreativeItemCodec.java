package science.atlarge.opencraft.opencraft.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.net.GlowBufUtils;
import science.atlarge.opencraft.opencraft.net.message.play.inv.CreativeItemMessage;

public final class CreativeItemCodec implements Codec<CreativeItemMessage> {

    @Override
    public CreativeItemMessage decode(ByteBuf buffer) {
        int slot = buffer.readShort();
        ItemStack item = GlowBufUtils.readSlot(buffer, true);
        return new CreativeItemMessage(slot, item);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, CreativeItemMessage message) {
        buffer.writeShort(message.getSlot());
        GlowBufUtils.writeSlot(buffer, message.getItem());
        return buffer;
    }
}
