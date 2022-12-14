package science.atlarge.opencraft.opencraft.net;

import org.json.simple.JSONObject;
import science.atlarge.opencraft.opencraft.entity.meta.MetadataIndex;
import science.atlarge.opencraft.opencraft.entity.meta.MetadataMap;
import science.atlarge.opencraft.opencraft.util.TextMessage;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utilities for helping with the protocol test.
 */
public final class ProtocolTestUtils {

    private ProtocolTestUtils() {
    }

    @SuppressWarnings("unchecked")
    public static JSONObject getJson() {
        JSONObject obj = new JSONObject();
        obj.put("key", "value");
        return obj;
    }

    public static TextMessage getTextMessage() {
        return new TextMessage("text");
    }

    public static List<MetadataMap.Entry> getMetadataEntry() {
        List<MetadataMap.Entry> list = new ArrayList<>();
        list.add(new MetadataMap.Entry(MetadataIndex.HEALTH, 1f));
        return list;
    }

    public static CompoundTag getTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("int", 5);
        tag.putString("string", "text");
        tag.putFloatList("list", Arrays.asList(1.f, 2.f, 3.f));
        tag.putCompound("compound", new CompoundTag());
        return tag;
    }
}
