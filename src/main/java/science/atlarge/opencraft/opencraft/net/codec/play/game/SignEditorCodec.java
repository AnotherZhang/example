package science.atlarge.opencraft.opencraft.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import org.bukkit.util.BlockVector;
import science.atlarge.opencraft.opencraft.net.GlowBufUtils;
import science.atlarge.opencraft.opencraft.net.message.play.game.SignEditorMessage;

public final class SignEditorCodec implements Codec<SignEditorMessage> {

    @Override
    public SignEditorMessage decode(ByteBuf buffer) {
        BlockVector position = GlowBufUtils.readBlockPosition(buffer);
        int x = position.getBlockX();
        int y = position.getBlockY();
        int z = position.getBlockZ();
        return new SignEditorMessage(x, y, z);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, SignEditorMessage message) {
        GlowBufUtils.writeBlockPosition(buffer, message.getX(), message.getY(), message.getZ());
        return buffer;
    }
}
