package com.pahlsoft.simpledata.clients;


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

class ClickhouseConnectionTest {

    static Logger log = LoggerFactory.getLogger(ClickhouseConnectionTest.class);
    private static ClickHouseContainer container;

    private static URL clickhouseURL;

   private static Configuration configuration;
   private static Workload workload;
   private static ClickHouseClient chClient;

   // private static List<Map<String, Object>> workloadMap = new ArrayList<>();

    @BeforeAll
    static void setup() throws Exception {

        // Build TestContainer
        container = new ClickHouseContainer("clickhouse/clickhouse-server")
                .withReuse(true)
                .withDatabaseName("TESTCHDB")
                .withUsername("default")
                .withPassword( "letme1n");
        container.start();
        log.info("JDBC access for Clickhouse Port Map 9000->" + container.getMappedPort(9000));
        log.info("HTTP/CLIENT access for Clickhouse Port Map 8123->" + container.getMappedPort(8123));

        loadConfigAndWorkload();

        chClient = new ClickHouseClient(configuration, workload);

        //buildAndIndexSingleDocument();

    }

    private static void loadConfigAndWorkload() {

        //        // Sample Data for Workload
//        Map<String, Object> singleWorkload = new HashMap<>();
//        singleWorkload.put("name", "product.category");
//        singleWorkload.put("type", "random_string_from_list");
//        singleWorkload.put("custom_list", new String("appliances, computers, diy, tools, televisions, audio"));
//
//        workloadMap.add(singleWorkload);
//        workload.setFields(workloadMap);

        // Stubbed Configuration for SDG
        configuration = new Configuration();
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
        workload.setIndexName("junit-test-index");
        workload.setWorkloadThreads(1);
        workload.setWorkloadSleep(1000);
        workload.setPrimaryShardCount(1);
        workload.setReplicaShardCount(1);
        workload.setPeakTime("19:00:00");
        workload.setPurgeOnStart(false);
        workload.setBackendBulkQueueDepth(0);
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
     void createTestTable() throws Exception {
        // SQL query to create a table
        String sqlQuery = "CREATE TABLE IF NOT EXISTS dude (" +
                "id Int32, " +
                "name String" +
                ") ENGINE = MergeTree() " +
                "ORDER BY id";
        Assertions.assertEquals(200,chClient.executeQuery(sqlQuery));
    }

//    @Test
//    void buildTableFromWorkloadTest() throws Exception {
//        Assert.assertTrue(true); //TODO: Stubbed out
//    }
//
//
//    @Test
//     void buildAndInsertSingleRecordFromWorkloadTest() throws Exception {
//        //TODO: Create a record in the test table.  Following tests will work after this one is created.
//    }

}