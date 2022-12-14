package science.atlarge.opencraft.opencraft.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import org.bukkit.util.BlockVector;
import science.atlarge.opencraft.opencraft.net.GlowBufUtils;
import science.atlarge.opencraft.opencraft.net.message.play.player.TabCompleteMessage;

import java.io.IOException;

public final class TabCompleteCodec implements Codec<TabCompleteMessage> {

    @Override
    public TabCompleteMessage decode(ByteBuf buffer) throws IOException {
        String text = ByteBufUtils.readUTF8(buffer);
        boolean assumeCommand = buffer.readBoolean();
        boolean hasLocation = buffer.readBoolean();
        BlockVector location = null;
        if (hasLocation) {
            location = GlowBufUtils.readBlockPosition(buffer);
        }
        return new TabCompleteMessage(text, assumeCommand, location);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, TabCompleteMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buffer, message.getText());
        buffer.writeBoolean(message.isAssumeCommand());
        BlockVector location = message.getLocation();
        if (location != null) {
            buffer.writeBoolean(true);
            GlowBufUtils.writeBlockPosition(buffer, location);
        } else {
            buffer.writeBoolean(false);
        }
        return buffer;
    }
}
