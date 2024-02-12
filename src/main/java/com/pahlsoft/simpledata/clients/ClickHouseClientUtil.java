package com.pahlsoft.simpledata.clients;

import com.pahlsoft.simpledata.generator.WorkloadGeneratorSQL;
import com.pahlsoft.simpledata.interfaces.ClientUtil;
import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClickHouseClientUtil implements ClientUtil {

    static Logger log = LoggerFactory.getLogger(ClickHouseClientUtil.class);

    private static ClickHouseClient clickHouseClient = null;


    public static ClickHouseClient createClient(final Configuration configuration, final Workload workload) {
        try {
            setupClickHouse(configuration,workload);
        } catch (Exception e) {
            log.error("Unable to setup Clickhouse Client.");
            log.error(e.getMessage());
        }
        return clickHouseClient;
    }

    public static void setupClickHouse(Configuration configuration, Workload workload) throws Exception {
        clickHouseClient = new ClickHouseClient(configuration);

        if (workload.getPurgeOnStart()) {
            purgeOnStart(workload);
            createClickhouseDatabase(workload);
            createClickHouseTable(workload);
        } else {
            createClickhouseDatabase(workload);
            createClickHouseTable(workload);
        }

    }

    //TODO: Implement TLS handling of URL for HTTP Client


    private static void createClickhouseDatabase(Workload workload) throws Exception {
        String sqlQuery = "CREATE DATABASE " + workload.getDatabaseName();
        try {
            log.info("Creating Database {}", workload.getDatabaseName());
            clickHouseClient.executeQuery(sqlQuery);
        } catch (Exception e) {
            log.error("Unable To Create Database: " + workload.getDatabaseName());
            log.error(e.getMessage());

        }
    }


    private static void createClickHouseTable(Workload workload) throws Exception {
        String sqlQuery = WorkloadGeneratorSQL.buildCreateTableStatement(workload);
        try {
            log.info("Creating Table {}", workload.getTableName());
            clickHouseClient.executeQuery(sqlQuery);
        } catch (Exception e) {
            log.error("Unable To Create Table {} for Database {}: ",workload.getTableName(),workload.getDatabaseName());
            log.error(e.getMessage());

        }
    }

    private static void purgeOnStart(Workload workload) throws Exception {
        String sqlQuery = "DROP DATABASE " + workload.getDatabaseName();
        try {
            log.info("Deleting Database {}", workload.getDatabaseName());
            clickHouseClient.executeQuery(sqlQuery);
        } catch (Exception e) {
            log.error("Unable to Delete Database {}", workload.getDatabaseName());
            log.error(e.getMessage());

        }

    }
}
