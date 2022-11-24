package science.atlarge.opencraft.opencraft.block.blocktype;

import org.bukkit.Material;
import science.atlarge.opencraft.opencraft.block.entity.BeaconEntity;
import science.atlarge.opencraft.opencraft.block.entity.BlockEntity;
import science.atlarge.opencraft.opencraft.chunk.GlowChunk;

public class BlockBeacon extends BlockDirectDrops {

    public BlockBeacon() {
        super(Material.BEACON);
    }

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new BeaconEntity(chunk.getBlock(cx, cy, cz));
    }
}
