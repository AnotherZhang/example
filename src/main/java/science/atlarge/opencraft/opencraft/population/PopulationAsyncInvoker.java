package science.atlarge.opencraft.opencraft.population;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.AWSLambdaAsyncClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import science.atlarge.opencraft.opencraft.measurements.EventLogger;
import science.atlarge.opencraft.opencraft.measurements.EventNoopLogger;
import science.atlarge.opencraft.opencraft.population.PopulateInfo.PopulateInput;
import science.atlarge.opencraft.opencraft.population.PopulateInfo.PopulateOutput;
import science.atlarge.opencraft.opencraft.serverless.HttpCallRes;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

public class PopulationAsyncInvoker {
    private final static AWSLambdaAsync client = getClient();

    private static AWSLambdaAsync getClient() {
        return getClient(System.getenv("LAMBDA_REGION"),
                System.getenv("LAMBDA_ACCESS_KEY"),
                System.getenv("LAMBDA_SECRET_KEY"));
    }

    private static AWSLambdaAsync getClient(String regionName, String accessKey, String secretKey) {
        Regions region = Regions.fromName(regionName);
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSLambdaAsyncClientBuilder builder = AWSLambdaAsyncClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region);

        return builder.build();
    }

    public static Future<InvokeResult> invoke(PopulateInput input, String functionName) {
        return invoke(input, functionName, new EventNoopLogger());
    }

    public static Future<InvokeResult> invoke(PopulateInput input, String functionName, EventLogger logger) {
        logger.start(String.format("serialize_input (%d,%d)", input.x, input.z));
        String inp = input.serialize();
        logger.stop(String.format("serialize_input (%d,%d)", input.x, input.z));

        // check the function is defined in the config; otherwise try to read from env variable
        if (functionName.equals("")) {
            functionName = System.getenv("LAMBDA_FUNCTION");
        }

        InvokeRequest req = new InvokeRequest()
                .withFunctionName(functionName)
                .withPayload(inp);

        return client.invokeAsync(req);
    }

    public static PopulateOutput fromResult(InvokeResult result) {
        // get text of response
        String serializedResponse = StandardCharsets.UTF_8.decode(result.getPayload()).toString();

        // deserialize
        return PopulateOutput.deserialize(serializedResponse);
    }

    /**
     * get Result from HttpCallRes, used by Azure invoker
     */

    public static PopulateInfo.PopulateOutput fromResult(HttpCallRes result) {
        String serializedResponse = result.getRes();
        return PopulateInfo.PopulateOutput.deserialize(serializedResponse, "azure");
    }
}
