package com.pahlsoft.simpledata.clients;

import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class RabbitMQClient {
    static Logger log = LoggerFactory.getLogger(RabbitMQClient.class);
    private static Configuration configuration = null;
    private static Connection rabbitMQConnection = null;
    private static Channel rabbitMQChannel = null;

    public RabbitMQClient(Configuration configuration) {
        this.configuration = configuration;

        try {
            this.rabbitMQConnection = createConnection();
            log.info("Created Rabbit MQ Connection");
            this.rabbitMQChannel = createChannel();
            log.info("Created Rabbit MQ Channel");
        } catch (Exception e) {
            log.error("Errors setting up Rabbit MQ Client.");
        }

    }

    private Connection createConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = null;

        factory.setHost(configuration.getBackendHost());
        factory.setPort(configuration.getBackendPort());
        factory.setUsername(configuration.getBackendUser());
        factory.setPassword(configuration.getBackendPassword());

        try {
            connection = factory.newConnection();
        } catch (Exception e) {
            log.error("Error creating connection to Rabbit MQ: {}", configuration.getBackendHost());
        }
        if (connection == null) log.error("Error creating connection to Rabbit MQ: {}", configuration.getBackendHost());

        return connection;

    }

    private Channel createChannel() {
        try {
            return rabbitMQConnection.createChannel();
        } catch (IOException e) {
            log.error("Error creating channel to Rabbit MQ: {}", configuration.getBackendHost());
            throw new RuntimeException(e);
        }
    }

    public static boolean publishMessage(String queue, String message)  {
        log.debug("DEBUG: Message being sent to Queue: {} ", queue);
        log.debug("DEBUG: Message being sent to RabbitMQ: {} ", message);

        try {

            rabbitMQChannel.basicPublish("", queue, null, message.getBytes(StandardCharsets.UTF_8));
            log.debug("DEBUG: Message being sent to RabbitMQ: {}",message);
            return true;
        } catch (IOException e) {
            log.error("Unable to send message to Queue {}: ", queue);
            log.error(e.getMessage());
            return false;
        }
    }


    public static boolean createQueue(Workload workload) {
        try {
            rabbitMQChannel.queueDeclare(workload.getQueueName(), false, false, false, null);
            log.info("Created Queue to Rabbit MQ: {}", workload.getQueueName());
            return true;
        } catch (IOException e) {
            log.error("Error creating Queue to Rabbit MQ: {}", workload.getQueueName());
            log.error(e.getMessage());
            return false;
        }

    }

    public static boolean deleteQueue(Workload workload) {
        try {
            rabbitMQChannel.queueDelete(workload.getQueueName());
            log.info("Deleted Queue to Rabbit MQ: {}", workload.getQueueName());
            return true;
        } catch (IOException e) {
            log.error("Error deleting Queue to Rabbit MQ: {}", workload.getQueueName());
            log.error(e.getMessage());
                return false;
        }
    }



}
