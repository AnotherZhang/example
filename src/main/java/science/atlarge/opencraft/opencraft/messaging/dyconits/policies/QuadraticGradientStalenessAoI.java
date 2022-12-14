package science.atlarge.opencraft.opencraft.messaging.dyconits.policies;

import com.flowpowered.network.Message;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import science.atlarge.opencraft.dyconits.Bounds;
import science.atlarge.opencraft.dyconits.Subscriber;
import science.atlarge.opencraft.dyconits.policies.DyconitCommand;
import science.atlarge.opencraft.dyconits.policies.DyconitPolicy;
import science.atlarge.opencraft.dyconits.policies.DyconitSubscribeCommand;
import science.atlarge.opencraft.dyconits.policies.DyconitUnsubscribeCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuadraticGradientStalenessAoI implements DyconitPolicy<Player, Message> {
    private static final String CATCH_ALL_DYCONIT_NAME = "catch-all";

    private final int viewDistance;
    private final Map<Player, Location> referenceLocation = new HashMap<>();
    private final Map<Player, Set<String>> prevSubscriptions = new HashMap<>();

    public QuadraticGradientStalenessAoI(int viewDistance) {
        this.viewDistance = viewDistance;
    }

    @NotNull
    @Override
    public String computeAffectedDyconit(@NotNull Object publisher) {
        if (publisher instanceof Chunk) {
            Chunk chunk = (Chunk) publisher;
            return chunkToName(chunk);
        }

        if (publisher instanceof Block) {
            Chunk chunk = ((Block) publisher).getChunk();
            return chunkToName(chunk);
        }

        if (publisher instanceof Entity) {
            Chunk chunk = ((Entity) publisher).getChunk();
            return chunkToName(chunk);
        }

        if (publisher instanceof Location) {
            Chunk chunk = ((Location) publisher).getChunk();
            return chunkToName(chunk);
        }

        return CATCH_ALL_DYCONIT_NAME;
    }

    private String chunkToName(Chunk chunk) {
        return chunk.getWorld().getName() + "-" + chunk.getX() + "-" + chunk.getZ();
    }

    @Override
    public @NotNull List<DyconitCommand<Player, Message>> update(Subscriber<Player, Message> sub) {
        Player player = sub.getKey();
        Location location = player.getLocation();
        List<DyconitCommand<Player, Message>> chunks = new ArrayList<>();

        if (referenceLocation.containsKey(player) && referenceLocation.get(player).distanceSquared(location) < 256) {
            return chunks;
        }
        referenceLocation.put(player, location);

        World world = location.getWorld();
        int centerX = location.getBlockX() >> 4;
        int centerZ = location.getBlockZ() >> 4;
        int radius = Math.min(viewDistance, sub.getKey().getViewDistance());

        Set<String> playerSubscriptions = new HashSet<>();
        chunks.add(new DyconitSubscribeCommand<>(sub.getKey(), sub.getCallback(), Bounds.Companion.getZERO(), CATCH_ALL_DYCONIT_NAME));
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                Chunk chunk = world.getChunkAt(x, z);
                String dyconitName = chunkToName(chunk);
                int distance = Math.min(Math.abs(centerX - x), Math.abs(centerZ - z));
                if (distance > 0) {
                    distance--;
                }
                chunks.add(new DyconitSubscribeCommand<>(sub.getKey(), sub.getCallback(), new Bounds(50 * distance * distance, -1), dyconitName));
                playerSubscriptions.add(dyconitName);
            }
        }
        Set<String> prevPlayerSubscriptions = prevSubscriptions.computeIfAbsent(player, p -> new HashSet<>());
        for (String dyconitName : prevPlayerSubscriptions) {
            if (!playerSubscriptions.contains(dyconitName)) {
                chunks.add(new DyconitUnsubscribeCommand<>(player, dyconitName));
            }
        }
        prevSubscriptions.put(player, playerSubscriptions);
        return chunks;
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
