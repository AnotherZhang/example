package science.atlarge.opencraft.opencraft.population;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import science.atlarge.opencraft.opencraft.measurements.EventLogger;
import science.atlarge.opencraft.opencraft.measurements.EventNoopLogger;
import science.atlarge.opencraft.opencraft.population.PopulateInfo.PopulateInput;
import science.atlarge.opencraft.opencraft.population.PopulateInfo.PopulateOutput;

import java.nio.charset.StandardCharsets;

public class PopulationInvoker {
    private final static AWSLambda client = getClient();

    private static AWSLambda getClient() {
        return getClient(System.getenv("LAMBDA_REGION"),
                System.getenv("LAMBDA_ACCESS_KEY"),
                System.getenv("LAMBDA_SECRET_KEY"));
    }

    private static AWSLambda getClient(String regionName, String accessKey, String secretKey) {
        Regions region = Regions.fromName(regionName);
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region);

        return builder.build();
    }

    public static PopulateOutput invoke(PopulateInput input, String functionName) {
        return invoke(input, functionName, new EventNoopLogger());
    }

    public static PopulateOutput invoke(PopulateInput input, String functionName, EventLogger logger) {
        // for async invoke: https://stackoverflow.com/questions/47345365/how-to-invoke-aws-lambda-function-from-another-lambda-function-and-return-withou/47350875
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

        // log time spent in lambda and invoke the function
        logger.start(String.format("lambda_time (%d,%d)", input.x, input.z));
        InvokeResult response = client.invoke(req);
        logger.stop(String.format("lambda_time (%d,%d)", input.x, input.z));

        // get text of response
        String serializedResponse = StandardCharsets.UTF_8.decode(response.getPayload()).toString();

        // deserialize
        return PopulateOutput.deserialize(serializedResponse);
    }
}
