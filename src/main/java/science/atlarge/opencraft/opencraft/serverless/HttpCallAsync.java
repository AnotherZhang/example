package science.atlarge.opencraft.opencraft.serverless;

import lombok.Getter;
import lombok.Setter;
import science.atlarge.opencraft.opencraft.GlowServer;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;

public class HttpCallAsync extends HttpCall implements Callable {
    @Getter
    @Setter
    private HttpCallRes httpCallRes = new HttpCallRes();
    @Getter
    @Setter
    private String payload;

    public HttpCallAsync(String urlStr) {
        super(urlStr);
    }

    @Override
    public HttpCallRes call() throws Exception {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.connect();

            if (payload != null) {
                OutputStream os = con.getOutputStream();
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
                pw.write(payload);
                pw.close();
            }

            httpCallRes.setRes(getResult(con));
            return httpCallRes;
        } catch (Exception e) {
            GlowServer.logger.warning("HttpCallAsync exception = " + e);
        }
        return null;
    }
}
