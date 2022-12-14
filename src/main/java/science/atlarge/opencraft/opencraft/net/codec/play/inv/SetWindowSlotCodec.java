package science.atlarge.opencraft.opencraft.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.net.GlowBufUtils;
import science.atlarge.opencraft.opencraft.net.message.play.inv.SetWindowSlotMessage;

public final class SetWindowSlotCodec implements Codec<SetWindowSlotMessage> {

    @Override
    public SetWindowSlotMessage decode(ByteBuf buffer) {
        byte id = buffer.readByte();
        short slot = buffer.readShort();
        ItemStack item = GlowBufUtils.readSlot(buffer);
        return new SetWindowSlotMessage(id, slot, item);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, SetWindowSlotMessage message) {
        buffer.writeByte(message.getId());
        buffer.writeShort(message.getSlot());
        GlowBufUtils.writeSlot(buffer, message.getItem());
        return buffer;
    }
}
