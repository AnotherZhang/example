package science.atlarge.opencraft.opencraft.population;

import science.atlarge.opencraft.opencraft.measurements.EventLogger;
import science.atlarge.opencraft.opencraft.measurements.EventNoopLogger;
import science.atlarge.opencraft.opencraft.serverless.HttpCallAsync;
import science.atlarge.opencraft.opencraft.serverless.HttpCallRes;
import science.atlarge.opencraft.opencraft.util.config.ServerConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PopulationAsyncInvokerAzure {
    protected static final String url = ServerConfig.getInstance().getString(ServerConfig.Key.OPENCRAFT_CHUNK_POPULATION_ENDPOINT);
    protected static final HttpCallAsync httpCallAsync = new HttpCallAsync(url);
    protected static ExecutorService executor = Executors.newFixedThreadPool(100);


    public static Future<HttpCallRes> invoke(PopulateInfo.PopulateInput input) {
        return invoke(input, new EventNoopLogger());
    }

    public static Future<HttpCallRes> invoke(PopulateInfo.PopulateInput input, EventLogger logger) {
        String payload = input.serialize("azure");
        httpCallAsync.setPayload(payload);
        return executor.submit(httpCallAsync);
    }


}
