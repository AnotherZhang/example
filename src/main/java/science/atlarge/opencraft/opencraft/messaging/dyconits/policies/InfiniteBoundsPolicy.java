package science.atlarge.opencraft.opencraft.messaging.dyconits.policies;

import com.flowpowered.network.Message;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import science.atlarge.opencraft.dyconits.Bounds;
import science.atlarge.opencraft.dyconits.Subscriber;
import science.atlarge.opencraft.dyconits.policies.DyconitCommand;
import science.atlarge.opencraft.dyconits.policies.DyconitPolicy;
import science.atlarge.opencraft.dyconits.policies.DyconitSubscribeCommand;

import java.util.Collections;
import java.util.List;

public class InfiniteBoundsPolicy implements DyconitPolicy<Player, Message> {

    public static String DYCONIT_NAME = "*";

    @NotNull
    @Override
    public String computeAffectedDyconit(@NotNull Object o) {
        return DYCONIT_NAME;
    }

    @NotNull
    @Override
    public List<DyconitCommand<Player, Message>> update(@NotNull Subscriber<Player, Message> subscriber) {
        return Collections.singletonList(new DyconitSubscribeCommand<>(subscriber.getKey(), subscriber.getCallback(), Bounds.Companion.getINFINITE(), DYCONIT_NAME));
    }

    @Override
    public int weigh(Message message) {
        return 1;
    }

    @NotNull
    @Override
    public List<DyconitCommand<Player, Message>> globalUpdate() {
        return Collections.emptyList();
    }
}
