package science.atlarge.opencraft.opencraft.command.minecraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.state.BlockStateData;
import science.atlarge.opencraft.opencraft.block.state.InvalidBlockStateException;
import science.atlarge.opencraft.opencraft.block.state.StateSerialization;
import science.atlarge.opencraft.opencraft.command.CommandUtils;
import science.atlarge.opencraft.opencraft.constants.ItemIds;
import science.atlarge.opencraft.opencraft.i18n.LocalizedStringImpl;
import science.atlarge.opencraft.opencraft.util.mojangson.Mojangson;
import science.atlarge.opencraft.opencraft.util.mojangson.ex.MojangsonParseException;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SetBlockCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public SetBlockCommand() {
        super("setblock");
        setPermission("minecraft.command.setblock"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length < 4) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }
        String itemName = CommandUtils.toNamespaced(args[3].toLowerCase());
        Material type = ItemIds.getBlock(itemName);
        if (type == null) {
            new LocalizedStringImpl("setblock.invalid.type",
                    commandMessages.getResourceBundle())
                    .sendInColor(ChatColor.RED, sender, itemName);
            return false;
        }
        Location location = CommandUtils
                .getLocation(CommandUtils.getLocation(sender), args[0], args[1], args[2]);
        GlowBlock block = (GlowBlock) location.getBlock();
        byte dataValue = 0;
        if (args.length > 4) {
            String state = args[4];
            BlockStateData data = CommandUtils.readState(sender, type, state);
            if (data == null) {
                return false;
            }
            if (data.isNumeric()) {
                dataValue = data.getNumericValue();
            } else {
                try {
                    dataValue = StateSerialization.parseData(type, data).getData();
                } catch (InvalidBlockStateException e) {
                    sender.sendMessage(ChatColor.RED + e.getMessage());
                    return false;
                }
            }
        }
        block.setType(type, dataValue, true);
        if (args.length > 5 && block.getBlockEntity() != null) {
            String dataTag = String
                    .join(" ", new ArrayList<>(Arrays.asList(args)).subList(5, args.length));
            try {
                CompoundTag prev = new CompoundTag();
                block.getBlockEntity().saveNbt(prev);
                CompoundTag tag = Mojangson.parseCompound(dataTag);
                tag.mergeInto(prev, true);
                block.getBlockEntity().loadNbt(prev);
            } catch (MojangsonParseException e) {
                commandMessages.getGeneric(GenericMessage.INVALID_JSON)
                        .sendInColor(ChatColor.RED, sender, e.getMessage());
                return false;
            }
        }
        new LocalizedStringImpl("setblock.done", commandMessages.getResourceBundle())
                .send(sender);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
            throws IllegalArgumentException {
        if (args.length == 4) {
            return ItemIds.getTabCompletion(args[3]);
        }
        return Collections.emptyList();
    }
}
