package science.atlarge.opencraft.opencraft.dispenser;

import lombok.RequiredArgsConstructor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.blocktype.BlockDispenser;
import science.atlarge.opencraft.opencraft.block.entity.state.GlowDispenser;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link DispenseBehavior} that launches a projectile.
 */
@RequiredArgsConstructor
public class ProjectileDispenseBehavior extends DefaultDispenseBehavior {

    /**
     * Creates an instance.
     *
     * @param projectileCreator a function that creates the projectile entity based on only the
     *                          location
     */
    public ProjectileDispenseBehavior(Function<? super Location, ? extends Projectile>
            projectileCreator) {
        this((location, ignoredItem) -> projectileCreator.apply(location));
    }

    /**
     * A function that creates the projectile entity.
     */
    private final BiFunction<? super Location, ? super ItemStack, ? extends Projectile>
            projectileCreator;

    @Override
    protected ItemStack dispenseStack(GlowBlock block, ItemStack stack) {
        GlowWorld world = block.getWorld();
        Vector position = BlockDispenser.getDispensePosition(block);
        BlockFace face = BlockDispenser.getFacing(block);
        Projectile entity = projectileCreator.apply(
                new Location(world, position.getX(), position.getY(), position.getZ()), stack);
        entity.setShooter(new GlowDispenser(block));
        entity.setVelocity(
                new Vector(face.getModX(), face.getModY() + 0.1f, face.getModZ()).multiply(6));

        stack.setAmount(stack.getAmount() - 1);
        if (stack.getAmount() < 1) {
            return null;
        }
        return stack;
    }

    @Override
    protected void playDispenseSound(GlowBlock block) {
        block.getWorld().playEffect(block.getLocation(), Effect.BOW_FIRE, 0);
    }

}
