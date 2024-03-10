package com.pahlsoft.simpledata.clients;

import com.pahlsoft.simpledata.interfaces.ClientUtil;
import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Properties;


public class KafkaClientUtil implements ClientUtil {

    static Logger log = LoggerFactory.getLogger(KafkaClientUtil.class);

    private static KafkaClient kafkaClient = null;


    public static KafkaClient createClient(final Configuration configuration, final Workload workload) {
        try {
            setupKafka(configuration, workload);
        } catch (Exception e) {
            log.error("Unable to create Kafka Client.");
            log.error(e.getMessage());
        }
        return kafkaClient;
    }

    public static void setupKafka(Configuration configuration, Workload workload) throws Exception {
        kafkaClient = new KafkaClient(configuration);
        if (workload.getPurgeOnStart()) {
            kafkaClient.deleteTopic(workload);
            kafkaClient.createTopic(workload);
        } else {
            kafkaClient.createTopic(workload);
        }
    }






}
