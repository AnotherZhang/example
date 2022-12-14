package science.atlarge.opencraft.opencraft.util.nbt;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link NbtInputStream} and reading from {@link CompoundTag}s.
 */
public class NbtInputTest {

    @Test
    public void helloWorld() throws IOException {
        InputStream raw = getClass().getResourceAsStream("/nbt/hello_world.nbt");
        assertThat("Failed to get test resource /nbt/hello_world.nbt", raw, notNullValue());
        try (NbtInputStream in = new NbtInputStream(raw, false)) {
            Checks.checkHelloWorld(in.readCompound());
        }
    }

    @Test
    public void bigTest() throws IOException {
        InputStream raw = getClass().getResourceAsStream("/nbt/bigtest.nbt");
        assertThat("Failed to get test resource /nbt/bigtest.nbt", raw, notNullValue());
        try (NbtInputStream in = new NbtInputStream(raw)) {
            Checks.checkBigTest(in.readCompound());
        }
    }

}
