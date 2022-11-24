package science.atlarge.opencraft.opencraft.command.glowstone;

import org.junit.Test;
import science.atlarge.opencraft.opencraft.command.CommandTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ColorCommandTest extends CommandTest<ColorCommand> {

    // TODO: Add more tests.

    public ColorCommandTest() {
        super(ColorCommand::new);
    }

    @Test
    @Override
    public void testExecuteWithoutPermission() {
        assertThat(command.execute(sender, "label", new String[0]), is(true));
    }
}

