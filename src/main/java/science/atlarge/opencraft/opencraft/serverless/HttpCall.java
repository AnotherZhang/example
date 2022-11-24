package science.atlarge.opencraft.opencraft.serverless;

import science.atlarge.opencraft.opencraft.GlowServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpCall {
    protected URL url;

    public HttpCall(String urlStr) {
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            GlowServer.logger.warning("Invalid Azure Function URL: " + urlStr);
        }
    }

    public String requestWithPayload(String payload) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.connect();

            OutputStream os = con.getOutputStream();
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
            pw.write(payload);
            pw.close();

            return getResult(con);
        } catch (Exception e) {
            GlowServer.logger.warning("HttpCall exception = " + e);
        }
        return null;
    }

    protected String getResult(HttpURLConnection con) throws IOException {
        InputStream is = con.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer sb = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }
}
