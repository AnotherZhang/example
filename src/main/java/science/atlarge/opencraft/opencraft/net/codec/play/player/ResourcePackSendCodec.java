package science.atlarge.opencraft.opencraft.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import science.atlarge.opencraft.opencraft.net.message.play.player.ResourcePackSendMessage;

import java.io.IOException;

public final class ResourcePackSendCodec implements Codec<ResourcePackSendMessage> {

    @Override
    public ResourcePackSendMessage decode(ByteBuf buffer) throws IOException {
        String url = ByteBufUtils.readUTF8(buffer);
        String hash = ByteBufUtils.readUTF8(buffer);
        return new ResourcePackSendMessage(url, hash);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, ResourcePackSendMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buffer, message.getUrl());
        ByteBufUtils.writeUTF8(buffer, message.getHash());
        return buffer;
    }
}
