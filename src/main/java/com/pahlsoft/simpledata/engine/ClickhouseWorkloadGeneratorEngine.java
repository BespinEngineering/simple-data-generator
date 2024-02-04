package com.pahlsoft.simpledata.engine;

import java.sql.*;
import java.text.MessageFormat;
import java.util.Properties;

import com.pahlsoft.simpledata.clients.ClickHouseClient;
import com.pahlsoft.simpledata.clients.ClickHouseClientUtil;
import com.pahlsoft.simpledata.interfaces.Engine;
import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClickhouseWorkloadGeneratorEngine implements Engine {
    static Logger log = LoggerFactory.getLogger(ElasticsearchWorkloadGeneratorEngine.class);

    private Workload workload;

    private ClickHouseClient chClient;

    public ClickhouseWorkloadGeneratorEngine(Configuration configuration, Workload workload) {
        this.workload = workload;
        // TODO: This might not be needed as we're using Traditional DB connections. Research using a connection pool.
        this.chClient = ClickHouseClientUtil.createClient(configuration, workload);
    }

    @Override
    public void run() {

        if (log.isInfoEnabled()) {
            log.info(MessageFormat.format( "Thread[{1}] Initiating ClickHouse Workload: {0}", workload.getWorkloadName(), Thread.currentThread().getId()));
            log.info("Thread[" + Thread.currentThread() + "] Workload Thread Count: " + workload.getWorkloadThreads());
            log.info("Thread[" + Thread.currentThread() + "] Workload Sleep Time (ms): " + workload.getWorkloadSleep());
            log.info("Thread[" + Thread.currentThread() + "] Workload Index Primary Shard Count: " + workload.getPrimaryShardCount());
            log.info("Thread[" + Thread.currentThread() + "] Workload Index Replica Shard Count: " + workload.getReplicaShardCount());
            log.info("Thread[" + Thread.currentThread() + "] Purge on Start Setting: " + workload.getPurgeOnStart().toString());
            log.info("Thread[" + Thread.currentThread() + "] Target Index Name: " + workload.getIndexName());
            log.info("Thread[" + Thread.currentThread() + "] Bulk Queue Depth: " + workload.getBackendBulkQueueDepth());
        }

        //////////////////////////
        // MAIN ENGINE LOOP BELOW
        //////////////////////////

        boolean engineRun = true;
        while (engineRun) {
            try {
                try {
                    //Bulk Docs
                    if (workload.getBackendBulkQueueDepth() > 0) {

                    // Single Doc
                    } else {

                    }
                } catch (Exception e) {

                }
            } finally {

            }
        }



    }
}
