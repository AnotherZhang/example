package science.atlarge.opencraft.opencraft.command.minecraft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.ServerProvider;
import science.atlarge.opencraft.opencraft.command.CommandTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class StopCommandTest extends CommandTest<StopCommand> {
    private GlowServer server;

    public StopCommandTest() {
        super(StopCommand::new);
    }

    @BeforeEach
    @Override
    public void before() {
        super.before();
        server = Mockito.mock(GlowServer.class);
        ServerProvider.setMockServer(server);
        when(opSender.getServer()).thenReturn(server);
    }

    @Test
    public void testNoArgs() {
        assertThat(command.execute(opSender, "label", new String[0]), is(true));
        Mockito.verify(server).shutdown();
    }

    @Test
    public void testWithArgs() {
        assertThat(command.execute(opSender, "label",
                new String[]{"This", "is", "a", "custom", "shutdown", "message"}), is(true));
        Mockito.verify(server).shutdown("This is a custom shutdown message");
    }
}
