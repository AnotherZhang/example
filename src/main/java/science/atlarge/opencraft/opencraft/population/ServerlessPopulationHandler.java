package science.atlarge.opencraft.opencraft.population;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.bukkit.generator.BlockPopulator;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.chunk.GlowChunk;
import science.atlarge.opencraft.opencraft.population.PopulateInfo.PopulateInput;
import science.atlarge.opencraft.opencraft.population.PopulateInfo.PopulateOutput;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * Handler for requests to Lambda function.
 */
public class ServerlessPopulationHandler implements RequestHandler<String, String> {
    @Override
    public String handleRequest(String input, final Context context) {
        // for cold boot prevention
        if (input.equals("")) {
            return "";
        }

        PopulateInput deserialized = PopulateInput.deserialize(input);

        GlowWorld world = deserialized.world;
        world.setTemporaryBlockChangeMessages(new ArrayList<>());

        Random random = new Random(world.getSeed());
        long xrand = (random.nextLong() / 2 << 1) + 1;
        long zrand = (random.nextLong() / 2 << 1) + 1;
        random.setSeed(deserialized.x * xrand + deserialized.z * zrand ^ world.getSeed());

        // set the world field of the chunk manager
        world.getChunkManager().setWorld(world);

        // enable serverless on world
        world.setServerless(true);

        GlowChunk chunkToPopulate = world.getChunkAt(deserialized.x, deserialized.z);

        for (BlockPopulator p : world.getGenerator().getDefaultPopulators(world)) {
            p.populate(world, random, chunkToPopulate);
        }

        // only send messages that are outside of this chunk, because
        // information for the blocks inside this chunk get sent as chunk data
        world.setTemporaryBlockChangeMessages(
                world.getTemporaryBlockChangeMessages().stream().filter(msg -> {
                    int msgChunkX = msg.getX() >> 4;
                    int msgChunkZ = msg.getZ() >> 4;
                    return !deserialized.filterBCM ||
                            msgChunkX != chunkToPopulate.getX() ||
                            msgChunkZ != chunkToPopulate.getZ();
                }).collect(Collectors.toList())
        );

        return new PopulateOutput(world, chunkToPopulate).serialize();
    }
}
