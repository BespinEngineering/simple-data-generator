package com.pahlsoft.simpledata.clients;


import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ClickHouseClient {

    /* TODO: Until there's a RESTFUL friendly Clickhouse client for java,
    we'll use this approach and not implement the ClientUtil interface method createClient()
     */

    static Logger log = LoggerFactory.getLogger(ClickHouseClient.class);

    static private Configuration configuration;
    static private Workload workload;

    public ClickHouseClient(Configuration configuration, Workload workload) {
        this.configuration = configuration;
        this.workload = workload;
    }

    static public int executeQuery(String sqlQuery) {
        int responseCode = 0;
        try {
            // Setting up the HTTP connection
            URL url = new URL(configuration.getBackendScheme() + "://" + configuration.getBackendHost() + ":" + configuration.getBackendPort());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
                System.out.println("Query executed successfully");
            } else {
                System.out.println("HTTP error code: " + responseCode);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return responseCode;

    }

}
