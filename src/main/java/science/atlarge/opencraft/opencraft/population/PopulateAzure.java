package science.atlarge.opencraft.opencraft.population;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.bukkit.generator.BlockPopulator;
import science.atlarge.opencraft.opencraft.GlowWorld;
import science.atlarge.opencraft.opencraft.chunk.GlowChunk;
import science.atlarge.opencraft.opencraft.population.PopulateInfo.PopulateInput;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Azure Functions with HTTP Trigger.
 */
public class PopulateAzure {
    /**
     * This function listens at endpoint "/api/PopulateAzure". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/PopulateAzure
     * 2. curl {your host}/api/PopulateAzure?name=HTTP%20Query
     */
    @FunctionName("PopulateAzure")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        HttpResponseMessage empty = request.createResponseBuilder(HttpStatus.OK)
                .body("")
                .build();
        // Cold boot prevention
        if (!request.getBody().isPresent()) {
            return empty;
        }
        String input = request.getBody().get();
//        context.getLogger().info("Java HTTP trigger: " + input);
        if (input.equals("") || input.equals("\"\""))
            return empty;

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

        String output = new PopulateInfo.PopulateOutput(world, chunkToPopulate).serialize();
//        System.out.println(output);
        return request.createResponseBuilder(HttpStatus.OK)
                .body(output)
                .build();

    }
}

