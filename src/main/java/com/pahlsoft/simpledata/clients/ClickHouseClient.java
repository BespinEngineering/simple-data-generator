package com.pahlsoft.simpledata.clients;


import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class ClickHouseClient {

    /* TODO: Until there's a RESTFUL friendly Clickhouse client for java,
    we'll use this approach and not implement the ClientUtil interface method createClient()
     */

    static Logger log = LoggerFactory.getLogger(ClickHouseClient.class);

    static private Configuration configuration;

     public ClickHouseClient(Configuration configuration) {
        this.configuration = configuration;

    }

    static public int executeQuery(String sqlQuery) {
        int responseCode = 0;
        try {
            // Setting up the HTTP connection
            URL url = new URL(configuration.getBackendScheme() + "://" + configuration.getBackendHost() + ":" + configuration.getBackendPort());
            log.info("Connecting to Clickhouse : " + url.toString());
            if (configuration.getBackendScheme() == "https") {
                HttpsURLConnection connection = createSecureConnection(url);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Adding Basic Authentication header
                connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((configuration.getBackendUser() + ":" + configuration.getBackendPassword()).getBytes()));

                // Sending the SQL query
                byte[] out = sqlQuery.getBytes(StandardCharsets.UTF_8);
                int length = out.length;
                connection.setFixedLengthStreamingMode(length);
                connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
                connection.connect();
                try (var os = connection.getOutputStream()) {
                    os.write(out);
                }

                // Handling the response
                responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    log.debug("Query executed successfully");
                } else {
                    log.error("HTTP error code: " + responseCode);
                }
            } else {
                HttpURLConnection connection = createConnection(url);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Adding Basic Authentication header
                connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((configuration.getBackendUser() + ":" + configuration.getBackendPassword()).getBytes()));

                // Sending the SQL query
                byte[] out = sqlQuery.getBytes(StandardCharsets.UTF_8);
                int length = out.length;
                connection.setFixedLengthStreamingMode(length);
                connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
                connection.connect();
                try (var os = connection.getOutputStream()) {
                    os.write(out);
                }

                // Handling the response
                responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    log.debug("Query executed successfully");
                } else {
                    log.error("HTTP error code: " + responseCode);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return responseCode;

    }

    private static HttpsURLConnection createSecureConnection(URL url) throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        return connection;

    }

    private static HttpURLConnection createConnection(URL url) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return connection;
    }


}
