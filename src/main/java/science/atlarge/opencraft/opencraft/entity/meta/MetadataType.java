package science.atlarge.opencraft.opencraft.entity.meta;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.EulerAngle;
import science.atlarge.opencraft.opencraft.util.TextMessage;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

import java.util.UUID;

/**
 * The types of values that entity metadata can contain.
 */
@RequiredArgsConstructor
public enum MetadataType {
    BYTE(Byte.class, false),
    INT(Integer.class, false),
    FLOAT(Float.class, false),
    STRING(String.class, false),
    CHAT(TextMessage.class, false),
    ITEM(ItemStack.class, false),
    BOOLEAN(Boolean.class, false),
    VECTOR(EulerAngle.class, false),
    POSITION(BlockVector.class, false),
    OPTPOSITION(BlockVector.class, true),
    DIRECTION(Integer.class, false),
    OPTUUID(UUID.class, true),
    BLOCKID(Integer.class, false),
    NBTTAG(CompoundTag.class, false),
    ;

    @Getter
    private final Class<?> dataType;
    @Getter
    private final boolean optional;

    public static MetadataType byId(int id) {
        return values()[id];
    }

    public int getId() {
        return ordinal();
    }
}
