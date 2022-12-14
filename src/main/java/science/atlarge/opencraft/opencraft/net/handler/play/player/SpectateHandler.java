package science.atlarge.opencraft.opencraft.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.net.GlowSession;
import science.atlarge.opencraft.opencraft.net.message.play.player.SpectateMessage;

import java.util.Objects;

public final class SpectateHandler implements MessageHandler<GlowSession, SpectateMessage> {

    @Override
    public void handle(GlowSession session, SpectateMessage message) {

        GlowPlayer player = session.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR && !Objects
                .equals(player.getProfile().getId(), message.getTarget())) {
            Entity entity = Bukkit.getEntity(message.getTarget());

            if (entity != null) {
                player.setSpectatorTarget(entity);
            }
        }
    }
}
