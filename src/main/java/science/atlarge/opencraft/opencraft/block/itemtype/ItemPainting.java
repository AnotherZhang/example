package science.atlarge.opencraft.opencraft.block.itemtype;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.entity.objects.GlowPainting;
import science.atlarge.opencraft.opencraft.util.IntCoordinates2D;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparingInt;

public class ItemPainting extends ItemType {

    /**
     * Contains all Arts. Key is the size of the art in descending order.
     */
    private static final ListMultimap<IntCoordinates2D, Art> ART_BY_SIZE;

    static {
        ART_BY_SIZE = MultimapBuilder.treeKeys(
                reverseOrder(
                        comparingInt(IntCoordinates2D::getX)
                                .thenComparingInt(IntCoordinates2D::getZ)
                )
        ).arrayListValues().build();

        Arrays.stream(Art.values()).forEach(art -> ART_BY_SIZE
                .put(new IntCoordinates2D(art.getBlockHeight(), art.getBlockWidth()), art));
    }

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face,
            ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        Location center = target.getRelative(face).getLocation();
        GlowPainting painting = new GlowPainting(center, face);

        for (IntCoordinates2D widthAndHeight : ART_BY_SIZE.keySet()) {
            List<Art> arts = ART_BY_SIZE.get(widthAndHeight);
            painting.setArtInternal(arts.get(0));

            if (!painting.isObstructed()) {
                int i = ThreadLocalRandom.current().nextInt(0, arts.size());
                painting.setArtInternal(arts.get(i));
                return;
            }
        }

        player.playSound(center, Sound.ENTITY_PAINTING_PLACE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
    }
}
