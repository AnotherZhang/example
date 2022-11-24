package science.atlarge.opencraft.opencraft.command.minecraft;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import science.atlarge.opencraft.opencraft.command.CommandUtils;
import science.atlarge.opencraft.opencraft.i18n.LocalizedStringImpl;

import java.util.Collections;
import java.util.List;

/**
 * Stops or starts rain, but doesn't schedule another weather change in the future as /weather does.
 * This command was removed from Java Edition in 1.13, but is still in Bedrock Edition.
 */
public class ToggleDownfallCommand extends GlowVanillaCommand {

    public ToggleDownfallCommand() {
        super("toggledownfall");
        setPermission("minecraft.command.toggledownfall"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }

        final World world = CommandUtils.getWorld(sender);

        world.setThundering(!world.hasStorm());
        world.setStorm(!world.hasStorm());
        new LocalizedStringImpl("toggledownfall.done", commandMessages.getResourceBundle())
                .send(sender);

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
            throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
