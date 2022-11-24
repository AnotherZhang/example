package science.atlarge.opencraft.opencraft.command.opencraft;

import org.bukkit.command.CommandSender;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.chunk.ChunkManager;
import science.atlarge.opencraft.opencraft.command.minecraft.GlowVanillaCommand;
import science.atlarge.opencraft.opencraft.i18n.LocalizedStringImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class ChunkManagerCommand extends GlowVanillaCommand {

    public ChunkManagerCommand() {
        super("cm");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args, GlowVanillaCommand.CommandMessages localizedMessages) {
        final ResourceBundle resourceBundle = localizedMessages.getResourceBundle();
        final ChunkManager cm = ((GlowWorld) sender.getServer().getWorlds().get(0)).getChunkManager();


        if (args.length > 0) {
            String argument = args[0];
            if (argument.equals("print")) {
                return print(cm, sender, resourceBundle);
            } else if (argument.equals("unload")) {
                return unload(cm, sender, resourceBundle);
            }
        }
        new LocalizedStringImpl("cm.failed", resourceBundle).send(sender);
        return false;
    }

    private boolean unload(ChunkManager cm, CommandSender sender, ResourceBundle resourceBundle) {
        cm.unloadOldChunks();
        new LocalizedStringImpl("cm.done", resourceBundle).send(sender, "unloaded old chunks");
        return true;
    }

    private boolean print(ChunkManager cm, CommandSender sender, ResourceBundle resourceBundle) {
        try {
            Path prettyPrintedMap = cm.prettyPrintChunks();
            new LocalizedStringImpl("cm.done", resourceBundle).send(sender, prettyPrintedMap);
            return true;
        } catch (IOException e) {
            new LocalizedStringImpl("cm.failed", resourceBundle).send(sender);
            return false;
        }
    }
}
