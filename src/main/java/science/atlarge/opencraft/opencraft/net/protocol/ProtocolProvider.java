package science.atlarge.opencraft.opencraft.net.protocol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import science.atlarge.opencraft.opencraft.net.config.DnsEndpoint;
import science.atlarge.opencraft.opencraft.net.http.HttpClient;
import science.atlarge.opencraft.opencraft.util.config.ServerConfig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Enumeration of the different Minecraft protocol states.
 */
@Getter
@RequiredArgsConstructor
public class ProtocolProvider {
    public final HandshakeProtocol handshake;
    public final StatusProtocol status;
    public final LoginProtocol login;
    public final PlayProtocol play;

    /**
     * Constructor for the class.
     *
     * @param serverConfig the server config.
     */
    public ProtocolProvider(ServerConfig serverConfig) {
        List<DnsEndpoint> dnsEndpoints = serverConfig.getMapList(ServerConfig.Key.DNS_OVERRIDES)
                .stream()
                .map(DnsEndpoint::fromConfigMap)
                .collect(Collectors.toList());
        HttpClient httpClient = new HttpClient(dnsEndpoints);

        this.status = new StatusProtocol();
        this.login = new LoginProtocol(httpClient);
        this.handshake = new HandshakeProtocol(status, login);
        this.play = new PlayProtocol();
    }
}
