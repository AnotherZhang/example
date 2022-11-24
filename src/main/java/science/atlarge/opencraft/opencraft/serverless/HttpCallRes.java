package science.atlarge.opencraft.opencraft.serverless;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import science.atlarge.opencraft.opencraft.GlowServer;

/**
 * a simple class for async HTTP Call Results
 */
public class HttpCallRes {
    @Getter
    @Setter
    private Integer statusCode;

    @Getter
    @Setter
    private String res;

    public HttpCallRes(){}

    public JSONObject getJsonObj() {
        try {
            JSONObject resObj = (JSONObject) new JSONParser().parse(res);
            return resObj;
        } catch (ParseException e) {
            GlowServer.logger.warning("Error parsing JSON Object");
        }
        return null;
    }

    public JSONArray getJsonArr() {
        return null;
    }

}
