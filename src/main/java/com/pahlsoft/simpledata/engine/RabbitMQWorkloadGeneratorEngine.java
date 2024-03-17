package com.pahlsoft.simpledata.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pahlsoft.simpledata.clients.RabbitMQClient;
import com.pahlsoft.simpledata.clients.RabbitMQClientUtil;
import com.pahlsoft.simpledata.generator.WorkloadGeneratorJSON;
import com.pahlsoft.simpledata.interfaces.Engine;
import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class RabbitMQWorkloadGeneratorEngine implements Engine {

    static Logger log = LoggerFactory.getLogger(RabbitMQWorkloadGeneratorEngine.class);

    static private Workload workload;

    static private  RabbitMQClient rabbitMQClient;

    public RabbitMQWorkloadGeneratorEngine(Configuration configuration, Workload workload) {
        this.workload = workload;
        this.rabbitMQClient = RabbitMQClientUtil.createClient(configuration, workload);
    }

    @Override
    public void run() {

        if (log.isInfoEnabled()) {
            log.info(MessageFormat.format( "Thread[{1}] Initiating RabbitMQ Workload: {0}", workload.getWorkloadName(), Thread.currentThread().getId()));
            log.info("Thread[" + Thread.currentThread() + "] Workload Queue: " + workload.getQueueName());
            log.info("Thread[" + Thread.currentThread() + "] Workload Thread Count: " + workload.getWorkloadThreads());
            log.info("Thread[" + Thread.currentThread() + "] Workload Sleep Time (ms): " + workload.getWorkloadSleep());
            log.info("Thread[" + Thread.currentThread() + "] Purge on Start Setting: " + workload.getPurgeOnStart().toString());
            log.info("Thread[" + Thread.currentThread() + "] Bulk Queue Depth: " + workload.getBackendBulkQueueDepth());
        }

        //////////////////////////
        // MAIN ENGINE LOOP BELOW
        //////////////////////////
        ObjectMapper objectMapper = new ObjectMapper();
        boolean engineRun = true;
        while (engineRun) {
            try {
                //Bulk Records
                if (workload.getBackendBulkQueueDepth() > 0) {
                    int response = 0;

                    for (int bulkItems = 0; bulkItems < workload.getBackendBulkQueueDepth(); bulkItems++) {
                        try {
                            rabbitMQClient.publishMessage(workload.getQueueName(), objectMapper.writeValueAsString(WorkloadGeneratorJSON.buildDocument(workload)));
                        } catch (Exception e) {
                            log.error("Error Inserting Bulk {} Records", workload.getBackendBulkQueueDepth());
                            log.error("HTTP Response Code: {}",response);
                            log.error(e.getMessage());
                            engineRun=false;

                        }
                    }

                    // Single Record
                } else {

                    try {
                        rabbitMQClient.publishMessage(workload.getQueueName(), objectMapper.writeValueAsString(WorkloadGeneratorJSON.buildDocument(workload)));
                    } catch (Exception e) {
                        log.error("Error Inserting Single Message");
                        log.error(e.getMessage());
                        engineRun=false;

                    }

                }
                Thread.sleep(calculateSleepDuration());

            } catch (Exception e) {
                log.error("Error trying to initiate workload RabbitMQ Workload Engine");
                log.error(e.getMessage());
                engineRun=false;
            }

        }
    }

    private int calculateSleepDuration() {

        if (workload.getPeakTime() == null) {
            log.debug("Static Sleep Used");
            return workload.getWorkloadSleep();
        } else {
            // Get the current time
            LocalTime currentTime = LocalTime.now();

            LocalTime peakTime = LocalTime.parse(workload.getPeakTime(), DateTimeFormatter.ISO_LOCAL_TIME);

            // Calculate the time difference in seconds
            long timeDifference = ChronoUnit.SECONDS.between(currentTime, peakTime);

            // Assuming the maximum time difference is 1 day (86400 seconds) for the scaling
            double maxTimeDifference = 86400.0;

            // Calculate the position in the sine wave based on the time difference
            double position = (Math.PI / 2) * (timeDifference / maxTimeDifference);

            // Adjust the position for past peak time
            if (timeDifference < 0) {
                position = Math.PI - position;
            }

            // Calculate the sine wave value based on the position
            double sineValue = Math.sin(position) * Math.cos(position);

            // Scale the sine value to the range of 1 to 10000
            int value = (int) (1 + (9999 * sineValue));
            log.debug("Peak Time used to calculate Sleep at " +value + "ms)");
            return value;
        }
    }
}
