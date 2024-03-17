package com.pahlsoft.simpledata.clients;


import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

public class KafkaClient {

    static Logger log = LoggerFactory.getLogger(KafkaClient.class);

    static private KafkaProducer kafkaProducer;

    static private AdminClient kafkaAdminClient;

    static private Properties config = new Properties();

     public KafkaClient(Configuration configuration) throws Exception {

         config.put("client.id", InetAddress.getLocalHost().getHostName());
         config.put("bootstrap.servers", configuration.getBackendHost()+":" + configuration.getBackendPort());
         config.put("acks", "all");
         config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
         config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


         try {
             this.kafkaProducer = createProducer();
             log.info("Created Kafka Producer");
             this.kafkaAdminClient = createAdminClient();
             log.info("Created Kafka AdminClient");
         } catch (Exception e) {
             log.error("Error creating KafkaProducer");
         }

     }

    private KafkaProducer createProducer() throws Exception {
        kafkaProducer = new KafkaProducer<String, String>(config);
        return kafkaProducer;
    }

    @SuppressWarnings("unchecked")
    public static void publishMessage(String topic, String message) {
        log.debug("DEBUG: Message being sent to Kafka:{} ",message);
        kafkaProducer.send(new ProducerRecord<String, String>(topic,  message));
        kafkaProducer.flush();
    }

    private AdminClient createAdminClient() {
        AdminClient client = null;
        try {
            client = AdminClient.create(config);
            if (client == null) {
                log.error("Unable to Initialize Kafka Admin Client. Exiting.");
                System.exit(1);
            }
        } catch (Exception e) {
            log.error("Error creating Kafka Admin Client");
        }
        return client;
    }

    public static boolean createTopic(Workload workload) {
         boolean result = true;
         try {
             ArrayList<NewTopic> topics = new ArrayList<>();
             topics.add(new NewTopic(workload.getTopicName(),workload.getNumPartitions(), workload.getReplicationFactor()));
             CreateTopicsResult createTopicsResult = kafkaAdminClient.createTopics(topics);
             KafkaFuture<Void> future = createTopicsResult.values().get(workload.getTopicName());

             if (future != null) {
                 future.get();
                 log.info("Created Topic: {}", workload.getTopicName());
                 return result;
             }

         } catch (Exception e) {
        log.error("Error Creating Topic: {}", workload.getTopicName());
        result = false;
    }
        return result;

    }

    public static boolean deleteTopic(Workload workload) {
        boolean result = true;
        try {
            DeleteTopicsResult deleteTopicsResult = kafkaAdminClient.deleteTopics(Collections.singleton(workload.getTopicName()));
            KafkaFuture<Void> future = deleteTopicsResult.topicNameValues().get(workload.getTopicName());

            if (future != null) {
                future.get();
                log.info("Deleted Topic: {}", workload.getTopicName());
                return result;
            }
        } catch (Exception e) {
            log.error("Error Deleting Topic: {}", workload.getTopicName());
            result = false;
        }
        return result;

    }

}
