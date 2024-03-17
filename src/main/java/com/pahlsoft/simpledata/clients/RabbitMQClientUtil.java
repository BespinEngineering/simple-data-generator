package com.pahlsoft.simpledata.clients;

import com.pahlsoft.simpledata.interfaces.ClientUtil;
import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMQClientUtil implements ClientUtil {

    static Logger log = LoggerFactory.getLogger(RabbitMQClientUtil.class);

    private static RabbitMQClient rabbitMQClient = null;


    public static RabbitMQClient createClient(final Configuration configuration, final Workload workload) {
        try {
            setupRabbitMQ(configuration, workload);
        } catch (Exception e) {
            log.error("Unable to create RabbitMQ Client.");
            log.error(e.getMessage());
        }
        return rabbitMQClient;
    }

    public static void setupRabbitMQ(Configuration configuration, Workload workload) throws Exception {
       rabbitMQClient = new RabbitMQClient(configuration);
        if (workload.getPurgeOnStart()) {
            rabbitMQClient.deleteQueue(workload);
            rabbitMQClient.createQueue(workload);
        } else {
            rabbitMQClient.createQueue(workload);
        }
    }



}
