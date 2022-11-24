package science.atlarge.opencraft.opencraft.population;

import science.atlarge.opencraft.opencraft.measurements.EventLogger;
import science.atlarge.opencraft.opencraft.measurements.EventNoopLogger;
import science.atlarge.opencraft.opencraft.serverless.HttpCall;
import science.atlarge.opencraft.opencraft.util.config.ServerConfig;


public class PopulationInvokerAzure {
    protected static final String url = ServerConfig.getInstance().getString(ServerConfig.Key.OPENCRAFT_CHUNK_POPULATION_ENDPOINT);
    protected static final HttpCall httpcall = new HttpCall(url);


    public static PopulateInfo.PopulateOutput invoke(PopulateInfo.PopulateInput input) {
        return invoke(input, new EventNoopLogger());
    }

    public static PopulateInfo.PopulateOutput invoke(PopulateInfo.PopulateInput input, EventLogger logger) {

        String payload = input.serialize("azure");

        String response = httpcall.requestWithPayload(payload);
        if (response != null) {
            return PopulateInfo.PopulateOutput.deserialize(response, "azure");
        } else {
            return null;
        }
    }

    public static void preventLambdaColdBoot() {
        String payload = "";
        httpcall.requestWithPayload(payload);
    }
}
