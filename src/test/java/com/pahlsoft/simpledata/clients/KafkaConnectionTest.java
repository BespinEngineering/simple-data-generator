package com.pahlsoft.simpledata.clients;

import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import org.apache.kafka.clients.KafkaClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.Future;

public class KafkaConnectionTest {

    static Logger log = LoggerFactory.getLogger(KafkaConnectionTest.class);
    private static KafkaContainer container;

    private static Configuration configuration;
    private static Workload workload;
    private static KafkaClient kafkaClient;  //TODO: Build This and incorporate it all into the Client (Producer/Consumer)

    private static KafkaProducer kafkaProducer;

    private static List<Map<String, Object>> workloadMap = new ArrayList<>();

    @BeforeAll
    static void setup() throws Exception {
        container = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));
        container.start();
        loadConfigAndWorkload();
    }

    private static void loadConfigAndWorkload() throws Exception{
        log.info("Connecting to Kafka: {}",container.getBootstrapServers());
        System.out.println("Connecting to Kafka: " + container.getBootstrapServers());

        // Stubbed Configuration for SDG
        configuration = new Configuration();
        configuration.setBackendType("KAFKA");
        configuration.setBackendScheme("http");
        configuration.setBackendHost("localhost");
        configuration.setBackendPort(container.getMappedPort(9093)); // Port needed to talk as Kafka Client
        configuration.setBackendUser("");
        configuration.setBackendPassword("");
        configuration.setBackendApiKeyEnabled(false);
        configuration.setBackendApiKeySecret("");
        configuration.setKeystoreLocation(""); // Intentionally left empty for Unit testing w/ TestContainers
        configuration.setKeystorePassword(""); // Intentionally left empty for Unit testing w/ TestContainers

        // Workload for SDG
        workload = new Workload();
        workload.setWorkloadName("TestLoad");
        workload.setTopicName("JUNIT_TEST_TOPIC");
        workload.setWorkloadThreads(1);
        workload.setWorkloadSleep(1000);
//        workload.setPrimaryShardCount(1);
//        workload.setReplicaShardCount(1);
        workload.setPeakTime("19:00:00");
        workload.setPurgeOnStart(false);
        workload.setBackendBulkQueueDepth(0);
//
//        // Sample Data for Workload
//        Map<String, Object> sampleWorkloadInt = new HashMap<>();
//        sampleWorkloadInt.put("name", "product_category");
//        sampleWorkloadInt.put("type", "int");
//        workloadMap.add(sampleWorkloadInt);
//
//        Map<String, Object> sampleWorkloadFloat = new HashMap<>();
//        sampleWorkloadFloat.put("name", "product_serial");
//        sampleWorkloadFloat.put("type", "float");
//        workloadMap.add(sampleWorkloadFloat);
//
//        Map<String, Object> sampleWorkloadString = new HashMap<>();
//        sampleWorkloadString.put("name", "product_description");
//        sampleWorkloadString.put("type", "product_name");
//        sampleWorkloadString.put("primary_key", "yes");
//        workloadMap.add(sampleWorkloadString);
//
//        workload.setFields(workloadMap);

        Properties config = new Properties();
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("bootstrap.servers", container.getBootstrapServers());
        config.put("acks", "all");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer<String, String>(config);



    }

    @Test
    void publishMessage() throws Exception {
        Future<RecordMetadata> future = kafkaProducer.send(new ProducerRecord<String, String>("your-topic", "key", "value"));
        RecordMetadata metadata = future.get();
        Assertions.assertTrue(metadata.hasTimestamp());
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

}
