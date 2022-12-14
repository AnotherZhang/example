package science.atlarge.opencraft.opencraft.messaging.dyconits.policies;

import com.flowpowered.network.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import science.atlarge.opencraft.dyconits.DyconitSystem;
import science.atlarge.opencraft.dyconits.MessageListQueue;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.measurements.EventLogger;
import science.atlarge.opencraft.opencraft.messaging.DyconitMessaging;
import science.atlarge.opencraft.opencraft.net.GlowSession;

import java.util.ArrayList;
import java.util.List;

class ZeroBoundsPolicyTest {

    private DyconitMessaging messaging;
    private List<Message> messages;

    @Mock
    GlowPlayer mockPlayer;

    @Mock
    GlowSession mockSession;

    @Mock
    Channel mockChannel;

    @Mock
    ChannelFuture mockChannelFuture;

    @Mock
    EventLogger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        messaging = new DyconitMessaging(new DyconitSystem<>(new ZeroBoundsPolicy(), (player, message) -> true, MessageListQueue::new));
        messages = new ArrayList<>();
        GlowServer.eventLogger = logger;
        Mockito.when(mockPlayer.getSession()).thenReturn(mockSession);
        Mockito.when(mockSession.getChannel()).thenReturn(mockChannel);
        Mockito.when(mockChannel.write(ArgumentMatchers.any(Message.class))).then(
                invocation -> {
                    messages.add(invocation.getArgument(0));
                    return mockChannelFuture;
                }
        );
    }

    @Test
    void noMessageLeaks() throws InterruptedException {
        messaging.update(mockPlayer);
        int numMessages = 1024;
        for (int i = 0; i < numMessages; i++) {
            messaging.publish(0, new Message() {
            });
        }
        messaging.flush();
        Assertions.assertEquals(numMessages, messages.size());
    }
}
