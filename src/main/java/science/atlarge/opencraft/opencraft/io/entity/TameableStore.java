package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.passive.GlowTameable;
import science.atlarge.opencraft.opencraft.util.UuidUtils;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

import java.util.function.Function;

abstract class TameableStore<T extends GlowTameable> extends AgeableStore<T> {

    public TameableStore(Class<T> clazz, EntityType type, Function<Location, ? extends T> creator) {
        super(clazz, type, creator);
    }

    @Override
    public void load(T entity, CompoundTag compound) {
        // TODO make this better.
        super.load(entity, compound);
        if (compound.containsKey("OwnerUUID") && !compound.getString("OwnerUUID").isEmpty()) {
            entity.setOwnerUniqueId(UuidUtils.fromString(compound.getString("OwnerUUID")));
            if (Bukkit.getPlayer(entity.getOwnerUniqueId()) != null) {
                entity.setOwner(Bukkit.getPlayer(entity.getOwnerUniqueId()));
            }
        } else if (compound.containsKey("Owner") && !compound.getString("Owner").isEmpty()) {
            String playerName = compound.getString("Owner");
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            if (player.hasPlayedBefore()) {
                entity.setOwnerUniqueId(player.getUniqueId());
                if (Bukkit.getPlayer(entity.getOwnerUniqueId()) != null) {
                    entity.setOwner(Bukkit.getPlayer(entity.getOwnerUniqueId()));
                }
            }
        }
    }

    @Override
    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
        if (entity.getOwner() == null) {
            tag.putString("OwnerUUID", "");
        } else {
            tag.putString("OwnerUUID", UuidUtils.toString(entity.getOwner().getUniqueId()));
        }
    }
}
