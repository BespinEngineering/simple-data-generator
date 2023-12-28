package com.pahlsoft.simpledata.clients;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cat.HealthResponse;
import co.elastic.clients.elasticsearch.cat.health.HealthRecord;
import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.util.*;


class ElasticsearchConnectionTest {

    static Logger log = LoggerFactory.getLogger(ElasticsearchConnectionTest.class);

    private static ElasticsearchContainer container;

    private static Configuration configuration;
    private static Workload workload;

    private static ElasticsearchClient esClient;

    private static List<Map<String, Object>> workloadMap = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {

        // Build TestContainer
        container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.3")
                .withReuse(true)
                .withPassword( "letme1n");
        container.start();

        // Stubbed Configuration for SDG
        configuration = new Configuration();
        configuration.setElasticsearchScheme("https");
        configuration.setElasticsearchHost("localhost");
        configuration.setElasticsearchPort(container.getFirstMappedPort());
        configuration.setElasticsearchUser("elastic");
        configuration.setElasticsearchPassword("letme1n");
        configuration.setElasticsearchApiKeyEnabled(false);
        configuration.setElasticsearchApiKeyId("");
        configuration.setKeystoreLocation(""); // Intentionally left empty for Unit testing
        configuration.setKeystorePassword(""); // Intentionally left empty for Unit testing

        // Workload for SDG
        workload = new Workload();
        workload.setWorkloadName("TestLoad");
        workload.setIndexName("junit-test-index");
        workload.setWorkloadThreads(1);
        workload.setWorkloadSleep(1000);
        workload.setPrimaryShardCount(1);
        workload.setReplicaShardCount(1);
        workload.setPeakTime("19:00:00");
        workload.setPurgeOnStart(false);
        workload.setElasticsearchBulkQueueDepth(0);

        // Sample Data for Workload
        Map<String, Object> singleWorkload = new HashMap<>();
        singleWorkload.put("name", "product.category");
        singleWorkload.put("type", "random_string_from_list");
        singleWorkload.put("custom_list", List.of("appliances", "computers", "diy", "tools", "televisions", "audio"));

        workloadMap.add(singleWorkload);
        workload.setFields(workloadMap);

        esClient = ElasticsearchClientUtil.createClient(configuration,workload);
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @Test
    void checkClusterStatusIsGreen() throws Exception {
          HealthResponse response = esClient.cat().health();
          List<HealthRecord> healthRecords = response.valueBody();
          Assert.assertEquals("green", healthRecords.get(0).status());
    }



}