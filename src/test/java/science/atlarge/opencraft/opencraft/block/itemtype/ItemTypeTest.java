package science.atlarge.opencraft.opencraft.block.itemtype;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.inventory.GlowPlayerInventory;
import science.atlarge.opencraft.opencraft.testutils.ServerShim;

import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Superclass for tests of {@link ItemType} subclasses. Provides a mock player with a real inventory
 * and a location in a mock world.
 */
public abstract class ItemTypeTest {
    protected GlowPlayerInventory inventory;
    protected GlowPlayer player;
    protected World world;
    protected Location location;

    /**
     * Override this to work around https://github.com/mockito/mockito/issues/357 by removing
     * RETURNS_SMART_NULLS.
     *
     * @return a mock GlowPlayer
     */
    protected GlowPlayer mockPlayer() {
        return Mockito.mock(GlowPlayer.class, RETURNS_SMART_NULLS);
    }

    @BeforeEach
    public void setUp() {
        ServerShim.install();
        player = mockPlayer();
        inventory = new GlowPlayerInventory(player);
        when(player.getInventory()).thenReturn(inventory);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        world = mock(World.class, RETURNS_SMART_NULLS);
        location = new Location(world, 0, 0, 0);
    }
}
