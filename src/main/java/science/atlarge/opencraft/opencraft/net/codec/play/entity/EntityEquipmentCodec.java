package science.atlarge.opencraft.opencraft.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import org.bukkit.inventory.ItemStack;
import science.atlarge.opencraft.opencraft.net.GlowBufUtils;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityEquipmentMessage;

import java.io.IOException;

public final class EntityEquipmentCodec implements Codec<EntityEquipmentMessage> {

    @Override
    public EntityEquipmentMessage decode(ByteBuf buffer) throws IOException {
        int id = ByteBufUtils.readVarInt(buffer);
        int slot = ByteBufUtils.readVarInt(buffer);
        ItemStack stack = GlowBufUtils.readSlot(buffer);
        return new EntityEquipmentMessage(id, slot, stack);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, EntityEquipmentMessage message) {
        ByteBufUtils.writeVarInt(buffer, message.getId());
        ByteBufUtils.writeVarInt(buffer, message.getSlot());
        GlowBufUtils.writeSlot(buffer, message.getStack());
        return buffer;
    }
}
