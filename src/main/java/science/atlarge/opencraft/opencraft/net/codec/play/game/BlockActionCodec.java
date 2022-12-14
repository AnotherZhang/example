package science.atlarge.opencraft.opencraft.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import org.bukkit.util.BlockVector;
import science.atlarge.opencraft.opencraft.net.GlowBufUtils;
import science.atlarge.opencraft.opencraft.net.message.play.game.BlockActionMessage;

import java.io.IOException;

public final class BlockActionCodec implements Codec<BlockActionMessage> {

    @Override
    public BlockActionMessage decode(ByteBuf buffer) throws IOException {
        BlockVector vector = GlowBufUtils.readBlockPosition(buffer);
        int data1 = buffer.readByte();
        int data2 = buffer.readByte();
        int blockType = ByteBufUtils.readVarInt(buffer);
        return new BlockActionMessage(
                vector.getBlockX(),
                vector.getBlockY(),
                vector.getBlockZ(),
                data1,
                data2,
                blockType
        );
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, BlockActionMessage message) {
        GlowBufUtils.writeBlockPosition(buffer, message.getX(), message.getY(), message.getZ());
        buffer.writeByte(message.getData1());
        buffer.writeByte(message.getData2());
        ByteBufUtils.writeVarInt(buffer, message.getBlockType());
        return buffer;
    }
}
