package science.atlarge.opencraft.opencraft.command.minecraft;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import science.atlarge.opencraft.opencraft.GlowServer;
import science.atlarge.opencraft.opencraft.ServerProvider;
import science.atlarge.opencraft.opencraft.i18n.ConsoleMessages;
import science.atlarge.opencraft.opencraft.i18n.LocalizedStringImpl;

public class OpCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public OpCommand() {
        super("op");
        setPermission("minecraft.command.op"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length != 1) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }
        String name = args[0];
        GlowServer server = (GlowServer) ServerProvider.getServer();
        // asynchronously lookup player
        server.getOfflinePlayerAsync(name).whenCompleteAsync((player, ex) -> {
            if (ex != null) {
                new LocalizedStringImpl("op.failed", commandMessages.getResourceBundle())
                        .sendInColor(ChatColor.RED, sender, name, ex.getMessage());
                ConsoleMessages.Error.Command.OP_FAILED.log(ex, name);
                return;
            }
            player.setOp(true);
            new LocalizedStringImpl("op.done", commandMessages.getResourceBundle())
                    .send(sender, name);
        });
        // todo: asynchronous command callbacks?
        return true;
    }
}
