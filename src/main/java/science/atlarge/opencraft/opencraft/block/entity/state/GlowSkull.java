package science.atlarge.opencraft.opencraft.block.entity.state;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.block.blocktype.BlockSkull;
import science.atlarge.opencraft.opencraft.block.entity.SkullEntity;
import science.atlarge.opencraft.opencraft.entity.meta.profile.GlowPlayerProfile;
import science.atlarge.opencraft.opencraft.util.Position;

public class GlowSkull extends GlowBlockState implements Skull {

    @Getter
    private SkullType skullType;
    private GlowPlayerProfile owner;
    @Getter
    @Setter
    private BlockFace rotation;

    /**
     * Creates the instance for the given block.
     *
     * @param block a head/skull block
     */
    public GlowSkull(GlowBlock block) {
        super(block);
        skullType = BlockSkull.getType(getBlockEntity().getType());
        rotation = Position.getDirection(getBlockEntity().getRotation());
        owner = getBlockEntity().getOwner();
    }

    public SkullEntity getBlockEntity() {
        return (SkullEntity) getBlock().getBlockEntity();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            SkullEntity skull = getBlockEntity();
            skull.setType(BlockSkull.getType(skullType));
            if (BlockSkull.canRotate((org.bukkit.material.Skull) getBlock().getState().getData())) {
                skull.setRotation(Position.getDirection(rotation));
            }
            if (skullType == SkullType.PLAYER) {
                skull.setOwner(owner);
            }
            getBlockEntity().updateInRange();
        }
        return result;
    }

    @Override
    public boolean hasOwner() {
        return owner != null;
    }

    @Override
    public String getOwner() {
        return hasOwner() ? owner.getName() : null;
    }

    @Override
    public boolean setOwner(String name) {
        GlowPlayerProfile owner = GlowPlayerProfile.getProfile(name).join();
        if (owner == null) {
            return false;
        }
        this.owner = owner;
        setSkullType(SkullType.PLAYER);
        return true;
    }

    @Override
    public OfflinePlayer getOwningPlayer() {
        return Bukkit.getOfflinePlayer(owner.getId());
    }

    @Override
    public void setOwningPlayer(OfflinePlayer offlinePlayer) {
        this.owner = new GlowPlayerProfile(offlinePlayer.getName(), offlinePlayer.getUniqueId(),
                true);
    }

    @Override
    public void setSkullType(SkullType type) {
        if (type != SkullType.PLAYER) {
            owner = null;
        }
        this.skullType = type;
    }
}
