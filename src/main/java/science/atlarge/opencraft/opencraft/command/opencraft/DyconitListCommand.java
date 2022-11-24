package science.atlarge.opencraft.opencraft.command.opencraft;

import org.bukkit.command.CommandSender;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.command.minecraft.GlowVanillaCommand;
import science.atlarge.opencraft.opencraft.i18n.LocalizedStringImpl;
import science.atlarge.opencraft.opencraft.messaging.DyconitMessaging;
import science.atlarge.opencraft.opencraft.messaging.Messaging;

import java.util.List;
import java.util.ResourceBundle;

public class DyconitListCommand extends GlowVanillaCommand {

    public DyconitListCommand() {
        super("dclist");
    }

    @Override
    protected boolean execute(CommandSender sender, String commandLabel, String[] args, CommandMessages localizedMessages) {
        final ResourceBundle resourceBundle = localizedMessages.getResourceBundle();
        Messaging messaging = ((GlowWorld) sender.getServer().getWorlds().get(0)).getMessagingSystem();

        if (messaging instanceof DyconitMessaging) {
            DyconitMessaging dm = (DyconitMessaging) messaging;
            List<String> dyconits = dm.getDyconits();
            new LocalizedStringImpl("dclist.done", resourceBundle).send(sender, String.join("\n", dyconits));
            return true;
        } else {
            new LocalizedStringImpl("dclist.failed", resourceBundle).send(sender);
            return false;
        }
    }
}
