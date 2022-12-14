package science.atlarge.opencraft.opencraft.block.entity;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.SkullType;
import org.bukkit.material.Skull;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.block.GlowBlockState;
import science.atlarge.opencraft.opencraft.block.blocktype.BlockSkull;
import science.atlarge.opencraft.opencraft.block.entity.state.GlowSkull;
import science.atlarge.opencraft.opencraft.constants.GlowBlockEntity;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.entity.meta.profile.GlowPlayerProfile;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

public class SkullEntity extends BlockEntity {

    @Getter
    @Setter
    private byte type;
    @Getter
    @Setter
    private byte rotation;
    @Getter
    private GlowPlayerProfile owner;

    public SkullEntity(GlowBlock block) {
        super(block);
        setSaveId("minecraft:skull");
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        type = tag.getByte("SkullType");

        if (BlockSkull.canRotate((Skull) getBlock().getState().getData())) {
            rotation = tag.getByte("Rot");
        }
        if (tag.containsKey("Owner")) {
            CompoundTag ownerTag = tag.getCompound("Owner");
            owner = GlowPlayerProfile.fromNbt(ownerTag).join();
        } else if (tag.containsKey("ExtraType")) {
            // Pre-1.8 uses just a name, instead of a profile object
            String name = tag.getString("ExtraType");
            if (name != null && !name.isEmpty()) {
                owner = GlowPlayerProfile.getProfile(name).join();
            }
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putByte("SkullType", type);
        if (BlockSkull.canRotate((Skull) getBlock().getState().getData())) {
            tag.putByte("Rot", rotation);
        }
        if (type == BlockSkull.getType(SkullType.PLAYER) && owner != null) {
            tag.putCompound("Owner", owner.toNbt());
        }
    }

    @Override
    public GlowBlockState getState() {
        return new GlowSkull(block);
    }

    @Override
    public void update(GlowPlayer player) {
        super.update(player);
        CompoundTag nbt = new CompoundTag();
        GlowWorld world = player.getWorld();
        saveNbt(nbt);
        // TODO: it is possible that this causes a broadcast message to be sent multiple times.
        world.sendBlockEntityChange(getBlock().getLocation(), GlowBlockEntity.SKULL, nbt);
    }

    public void setOwner(GlowPlayerProfile owner) {
        this.owner = owner;
        type = BlockSkull.getType(SkullType.PLAYER);
    }
}
