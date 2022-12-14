package science.atlarge.opencraft.opencraft.command.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import science.atlarge.opencraft.opencraft.command.CommandTarget;
import science.atlarge.opencraft.opencraft.command.CommandUtils;
import science.atlarge.opencraft.opencraft.entity.GlowEntity;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.i18n.LocalizedStringImpl;
import science.atlarge.opencraft.opencraft.io.entity.EntityStorage;
import science.atlarge.opencraft.opencraft.util.mojangson.Mojangson;
import science.atlarge.opencraft.opencraft.util.mojangson.ex.MojangsonParseException;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

import java.util.Arrays;

public class TestForCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public TestForCommand() {
        super("testfor");
        setPermission("minecraft.command.testfor"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }

        if (args.length < 1) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }

        String name = args[0];
        Entity[] entities;
        if (name.startsWith("@")) {
            CommandTarget target = new CommandTarget(sender, name);
            entities = target.getMatched(CommandUtils.getLocation(sender));

            if (entities.length == 0) {
                commandMessages.getGeneric(GenericMessage.NO_MATCHES)
                        .sendInColor(ChatColor.RED, sender, name);
                return false;
            }
        } else {
            // TODO: Select custom-named non-player entities?
            GlowPlayer player = (GlowPlayer) Bukkit.getPlayerExact(args[0]);
            if (player == null) {
                commandMessages.getGeneric(GenericMessage.NO_SUCH_PLAYER)
                        .sendInColor(ChatColor.RED, sender, name);
                return false;
            } else {
                entities = new Entity[] {player};
            }
        }

        LocalizedStringImpl foundMessage = new LocalizedStringImpl("testfor.found",
                commandMessages.getResourceBundle());
        if (args.length >= 2) {
            String data = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            CompoundTag tag;
            try {
                tag = Mojangson.parseCompound(data);
            } catch (MojangsonParseException e) {
                commandMessages.getGeneric(GenericMessage.INVALID_JSON)
                        .sendInColor(ChatColor.RED, sender, e.getMessage());
                return false;
            }
            LocalizedStringImpl wrongDataMessage =
                    new LocalizedStringImpl("testfor.wrong-data",
                            commandMessages.getResourceBundle());
            for (Entity entity : entities) {
                if (entity instanceof GlowEntity) {
                    CompoundTag entityTag = new CompoundTag();
                    EntityStorage.save((GlowEntity) entity, entityTag);
                    if (tag.matches(entityTag)) {
                        foundMessage.send(sender, CommandUtils.getName(entity));
                    } else {
                        wrongDataMessage.sendInColor(ChatColor.RED, sender,
                                CommandUtils.getName(entity));
                    }
                }
            }
        } else {
            for (Entity entity : entities) {
                foundMessage.send(sender, CommandUtils.getName(entity));
            }
        }

        // TODO: When command blocks are implemented, this should be updated to output the number of
        // matching entities.
        return true;
    }
}
