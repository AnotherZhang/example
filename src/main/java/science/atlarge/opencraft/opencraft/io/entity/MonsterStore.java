package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.monster.GlowMonster;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;

import java.util.function.Function;

public class MonsterStore<T extends GlowMonster> extends EntityStore<T> {

    private final Function<Location, ? extends T> creator;

    public MonsterStore(Class<? extends T> clazz, EntityType type,
            Function<Location, ? extends T> creator) {
        super(clazz, type);
        this.creator = creator;
    }

    public MonsterStore(Class<? extends T> clazz, String type,
            Function<Location, ? extends T> creator) {
        super(clazz, type);
        this.creator = creator;
    }

    @Override
    public T createEntity(Location location, CompoundTag compound) {
        try {
            return creator.apply(location);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Not implemented yet.");
        }
    }

}
