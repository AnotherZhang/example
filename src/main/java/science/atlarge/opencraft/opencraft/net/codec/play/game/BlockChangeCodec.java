package science.atlarge.opencraft.opencraft.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import org.bukkit.util.BlockVector;
import science.atlarge.opencraft.opencraft.net.GlowBufUtils;
import science.atlarge.opencraft.opencraft.net.message.play.game.BlockChangeMessage;

import java.io.IOException;

public final class BlockChangeCodec implements Codec<BlockChangeMessage> {

    @Override
    public BlockChangeMessage decode(ByteBuf buffer) throws IOException {
        BlockVector pos = GlowBufUtils.readBlockPosition(buffer);
        int type = ByteBufUtils.readVarInt(buffer);
        return new BlockChangeMessage(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), type);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, BlockChangeMessage message) {
        GlowBufUtils.writeBlockPosition(buffer, message.getX(), message.getY(), message.getZ());
        ByteBufUtils.writeVarInt(buffer, message.getType());
        return buffer;
    }
}
