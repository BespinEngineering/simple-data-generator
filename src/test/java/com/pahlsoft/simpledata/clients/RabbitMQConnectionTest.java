package com.pahlsoft.simpledata.clients;

import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.*;
public class RabbitMQConnectionTest {

    static Logger log = LoggerFactory.getLogger(RabbitMQConnectionTest.class);
    private static RabbitMQContainer container;

    private static Configuration configuration;
    private static Workload workload;

    private static RabbitMQClient rabbitMQClient;

    private static List<Map<String, Object>> workloadMap = new ArrayList<>();

    @BeforeAll
    static void setup() throws Exception {
        container = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.7.25-management-alpine"));
        container.withReuse(true).withAdminPassword("letme1n");
        container.start();
        loadConfigAndWorkload();
    }

    private static void loadConfigAndWorkload() throws Exception{
        log.info("Connecting to RabbitMQ: {}",container.getHttpPort());
        System.out.println("Connecting to RabbitMQ: " + container.getHttpPort());

        // Stubbed Configuration for SDG
        configuration = new Configuration();
        configuration.setBackendType("RABBITMQ");
        configuration.setBackendScheme("http");
        configuration.setBackendHost("localhost");
        configuration.setBackendPort(container.getMappedPort(5672)); // Port needed to talk as RabbitMQ Client
        configuration.setBackendUser("guest");
        configuration.setBackendPassword("letme1n");
        configuration.setBackendApiKeyEnabled(false);
        configuration.setBackendApiKeySecret("");
        configuration.setKeystoreLocation(""); // Intentionally left empty for Unit testing w/ TestContainers
        configuration.setKeystorePassword(""); // Intentionally left empty for Unit testing w/ TestContainers

        // Workload for SDG
        workload = new Workload();
        workload.setWorkloadName("TestLoad");
        workload.setQueueName("JUNIT.TEST.QUEUE");
        workload.setNumPartitions(1);
        workload.setReplicationFactor((short)1);
        workload.setWorkloadThreads(1);
        workload.setWorkloadSleep(1000);
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

        rabbitMQClient = RabbitMQClientUtil.createClient(configuration, workload);

    }

    @Test
    void createQueueTest() throws Exception {
        Assertions.assertTrue(rabbitMQClient.createQueue(workload));
    }
    @Test
    void publishMessageTest() throws Exception {
        Assertions.assertTrue(rabbitMQClient.publishMessage("dude","message"));

    }

    @Test
    void deleteQueueTest() throws Exception {
        Assertions.assertTrue(rabbitMQClient.deleteQueue(workload));
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

}
