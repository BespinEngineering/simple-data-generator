package com.pahlsoft.simpledata.clients;


import com.pahlsoft.simpledata.generator.WorkloadGeneratorSQL;
import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.clickhouse.ClickHouseContainer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ClickhouseConnectionTest {

    static Logger log = LoggerFactory.getLogger(ClickhouseConnectionTest.class);
    private static ClickHouseContainer container;

   private static Configuration configuration;
   private static Workload workload;
   private static ClickHouseClient chClient;

   private static List<Map<String, Object>> workloadMap = new ArrayList<>();

    @BeforeAll
    static void setup() throws Exception {

        // Build TestContainer
        container = new ClickHouseContainer("clickhouse/clickhouse-server")
                .withReuse(true)
                .withUsername("default")
                .withPassword( "letme1n");
        container.start();
        log.info("JDBC access for Clickhouse Port Map 9000->" + container.getMappedPort(9000));
        log.info("HTTP/CLIENT access for Clickhouse Port Map 8123->" + container.getMappedPort(8123));

        loadConfigAndWorkload();
        chClient = new ClickHouseClient(configuration);

    }

    private static void loadConfigAndWorkload() {

        // Stubbed Configuration for SDG
        configuration = new Configuration();
        configuration.setBackendType("CLICKHOUSE");
        configuration.setBackendScheme("http");  //TODO: Make this secure with TLS
        configuration.setBackendHost("localhost");
        configuration.setBackendPort(container.getMappedPort(8123)); // Port needed to talk as CH Client
        configuration.setBackendUser("default");
        configuration.setBackendPassword("letme1n");
        configuration.setBackendApiKeyEnabled(false);
        configuration.setBackendApiKeySecret("");
        configuration.setKeystoreLocation(""); // Intentionally left empty for Unit testing w/ TestContainers
        configuration.setKeystorePassword(""); // Intentionally left empty for Unit testing w/ TestContainers

        // Workload for SDG
        workload = new Workload();
        workload.setWorkloadName("TestLoad");
        workload.setDatabaseName("JUNIT_TEST_DATABASE");
        workload.setTableName("JUNIT_TEST_TABLE");
        workload.setBackendEngine("MergeTree");   //MergeTree is self managed, ReplicatedMergeTree is Cloud Service
        workload.setWorkloadThreads(1);
        workload.setWorkloadSleep(1000);
        workload.setPrimaryShardCount(1);
        workload.setReplicaShardCount(1);
        workload.setPeakTime("19:00:00");
        workload.setPurgeOnStart(false);
        workload.setBackendBulkQueueDepth(0);

        // Sample Data for Workload
        Map<String, Object> sampleWorkloadInt = new HashMap<>();
        sampleWorkloadInt.put("name", "product_category");
        sampleWorkloadInt.put("type", "int");
        workloadMap.add(sampleWorkloadInt);

        Map<String, Object> sampleWorkloadFloat = new HashMap<>();
        sampleWorkloadFloat.put("name", "product_serial");
        sampleWorkloadFloat.put("type", "float");
        workloadMap.add(sampleWorkloadFloat);

        Map<String, Object> sampleWorkloadString = new HashMap<>();
        sampleWorkloadString.put("name", "product_description");
        sampleWorkloadString.put("type", "product_name");
        sampleWorkloadString.put("primary_key", "yes");
        workloadMap.add(sampleWorkloadString);

        workload.setFields(workloadMap);

    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @Test
    void validateTestContainerClickhouseHTTPPort() throws Exception {
        Assertions.assertNotEquals(9000,container.getMappedPort(9000));
    }

    @Test
    void validateTestContainerClickhouseJDBCPort() throws Exception {
        Assertions.assertNotEquals(8123,container.getMappedPort(8123));
    }

    @Test
    void validateCreateClientConnection() throws Exception {
        // Execute Simple Select Statement
        String sqlQuery = "SELECT 1";

        Assertions.assertEquals(200,chClient.executeQuery(sqlQuery));
    }

    @Test
    void createDatabase() throws Exception {
        String sqlQuery = "CREATE DATABASE " + workload.getDatabaseName();
        Assertions.assertEquals(200,chClient.executeQuery(sqlQuery));
    }

    @Test
    void createTableFromWorkload() throws Exception {
        String sqlQuery = WorkloadGeneratorSQL.buildCreateTableStatement(workload);
        Assertions.assertEquals(200,chClient.executeQuery(sqlQuery));
        String validateTableQuery = "SELECT * FROM " + workload.getDatabaseName() + "." + workload.getTableName();
        Assertions.assertEquals(200,chClient.executeQuery(validateTableQuery));
    }



    @Test
     void buildAndInsertSingleRecordFromWorkload() throws Exception {
        String sqlQuery = WorkloadGeneratorSQL.buildSingleRecord(workload);
        Assertions.assertEquals(200,chClient.executeQuery(sqlQuery));
    }

}