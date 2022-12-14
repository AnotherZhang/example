package science.atlarge.opencraft.opencraft.net.handler.login;

import com.flowpowered.network.MessageHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import science.atlarge.opencraft.opencraft.EventFactory;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.entity.meta.profile.GlowPlayerProfile;
import science.atlarge.opencraft.opencraft.net.GlowSession;
import science.atlarge.opencraft.opencraft.net.ProxyData;
import science.atlarge.opencraft.opencraft.net.message.login.EncryptionKeyRequestMessage;
import science.atlarge.opencraft.opencraft.net.message.login.LoginStartMessage;
import science.atlarge.opencraft.opencraft.util.SecurityUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class LoginStartHandler implements MessageHandler<GlowSession, LoginStartMessage> {

    @Override
    public void handle(GlowSession session, LoginStartMessage message) {
        String name = message.getUsername();
        GlowServer server = session.getServer();

        if (server.getOnlineMode()) {
            // Get necessary information to create our request message
            String sessionId = session.getSessionId();
            byte[] publicKey = SecurityUtils
                    .generateX509Key(server.getKeyPair().getPublic())
                    .getEncoded(); //Convert to X509 format
            byte[] verifyToken = SecurityUtils.generateVerifyToken();

            // Set verify data on session for use in the response handler
            session.setVerifyToken(verifyToken);
            session.setVerifyUsername(name);

            // Send created request message and wait for the response
            session.send(new EncryptionKeyRequestMessage(sessionId, publicKey, verifyToken));
        } else {
            GlowPlayerProfile profile;
            ProxyData proxy = session.getProxyData();

            if (proxy == null) {
                UUID uuid = UUID
                        .nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
                profile = new GlowPlayerProfile(name, uuid, true);
            } else {
                profile = proxy.getProfile();
                if (profile == null) {
                    profile = proxy.getProfile(name);
                }
            }

            AsyncPlayerPreLoginEvent event = EventFactory.getInstance()
                    .onPlayerPreLogin(profile.getName(), session.getAddress(), profile.getId());
            if (event.getLoginResult() != Result.ALLOWED) {
                session.disconnect(event.getKickMessage(), true);
                return;
            }

            GlowPlayerProfile finalProfile = profile;
            server.getScheduler().runTask(null, () -> session.setPlayer(finalProfile));
        }
    }
}
