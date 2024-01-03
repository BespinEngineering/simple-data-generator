package com.pahlsoft.simpledata.clients;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cat.HealthResponse;
import co.elastic.clients.elasticsearch.cat.health.HealthRecord;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pahlsoft.simpledata.generator.WorkloadGenerator;
import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;


class ElasticsearchConnectionTest {

    static Logger log = LoggerFactory.getLogger(ElasticsearchConnectionTest.class);
    private static ElasticsearchContainer container;
    private static Configuration configuration;
    private static Workload workload;
    private static ElasticsearchClient esClient;
    private static List<Map<String, Object>> workloadMap = new ArrayList<>();

    @BeforeAll
    static void setup() throws Exception {

        // Build TestContainer
        container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.2")
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
        configuration.setKeystoreLocation(""); // Intentionally left empty for Unit testing w/ TestContainers
        configuration.setKeystorePassword(""); // Intentionally left empty for Unit testing w/ TestContainers

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
        singleWorkload.put("custom_list", new String("appliances, computers, diy, tools, televisions, audio"));

        workloadMap.add(singleWorkload);
        workload.setFields(workloadMap);

        esClient = ElasticsearchClientUtil.createClient(configuration,workload);

        //Index One Doc
        buildAndIndexSingleDocument();
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @Test
    void checkClusterStatusIsYellow() throws Exception {
          HealthResponse response = esClient.cat().health();
          List<HealthRecord> healthRecords = response.valueBody();
          Assert.assertEquals("yellow", healthRecords.get(0).status());
    }

    @Test
    void checkForIndexTemplate() throws Exception {
        BooleanResponse templateResponse = esClient.indices().existsIndexTemplate(builder -> builder.name(workload.getIndexName() + "_template"));
        Assert.assertTrue(templateResponse.value());
    }

    @Test
    void checkIndexSettingShardCounts() throws Exception {
        GetIndexResponse getIndexResponse = esClient.indices().get(g -> g.index(workload.getIndexName()));
        IndexSettings settings = getIndexResponse.get(workload.getIndexName()).settings();
        Assert.assertEquals( "1", settings.index().numberOfShards());
        Assert.assertEquals( "1", settings.index().numberOfReplicas());
    }


    static void buildAndIndexSingleDocument() throws Exception {
        //TODO: Create a document in the test index.  Following tests will work after this one is created.
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(WorkloadGenerator.buildDocument(workload));
        Reader input = new StringReader(json);
        IndexRequest<JsonData> request = IndexRequest.of(i -> i
                .index(workload.getIndexName())
                .withJson(input)
        );
        IndexResponse response = esClient.index(request);
        log.debug("Document " + response.id() + " Indexed with version " + response.version());
        Assert.assertEquals(1,response.version());
    }


}