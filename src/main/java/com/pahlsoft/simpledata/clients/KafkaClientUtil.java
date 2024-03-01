package com.pahlsoft.simpledata.clients;

import com.pahlsoft.simpledata.generator.WorkloadGeneratorSQL;
import com.pahlsoft.simpledata.interfaces.ClientUtil;
import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Properties;

public class KafkaClientUtil implements ClientUtil {

    static Logger log = LoggerFactory.getLogger(KafkaClientUtil.class);

    private static KafkaClient kafkaClient = null;

    public static KafkaClient createProducer(final Configuration configuration, final Workload workload) {
        try {
            setupKafka(configuration);
        } catch (Exception e) {
            log.error("Unable to create Kafka Client.");
            log.error(e.getMessage());
        }
        return kafkaClient;
    }

    public static void setupKafka(Configuration configuration) throws Exception {
        kafkaClient = new KafkaClient(configuration);
    }



}
