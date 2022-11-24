package science.atlarge.opencraft.opencraft.io.entity;

import org.bukkit.entity.EntityType;
import science.atlarge.opencraft.opencraft.entity.monster.GlowEvoker;

public class EvokerStore extends MonsterStore<GlowEvoker> {

    public EvokerStore() {
        super(GlowEvoker.class, EntityType.EVOKER, GlowEvoker::new);
    }
}
