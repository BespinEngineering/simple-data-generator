package com.pahlsoft.simpledata.runner;

import com.pahlsoft.simpledata.clients.ElasticsearchClientUtil;
import com.pahlsoft.simpledata.engine.ClickhouseWorkloadGeneratorEngine;
import com.pahlsoft.simpledata.engine.ElasticsearchWorkloadGeneratorEngine;
import com.pahlsoft.simpledata.engine.KafkaWorkloadGeneratorEngine;
import com.pahlsoft.simpledata.model.Configuration;
import com.pahlsoft.simpledata.model.Workload;
import com.pahlsoft.simpledata.threader.WorkloadGeneratorEngineThreader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;


public class EngineRunner {
    static Logger log = LoggerFactory.getLogger(EngineRunner.class);

    public static void main(String[] args) {
        validateArguments(args);
        Configuration configuration = loadConfig(args[0]);

        try {
            // Create Engine for each workload
            Iterator iterator = configuration.getWorkloads().iterator();

            while (iterator.hasNext()) {
                Workload workload;
                workload = (Workload) iterator.next();

                switch (configuration.getBackendType())  {
                    case "ELASTICSEARCH":
                        ElasticsearchClientUtil.setupElasticsearch(configuration,workload);
                        WorkloadGeneratorEngineThreader.runEngine(workload.getWorkloadThreads(), new ElasticsearchWorkloadGeneratorEngine(configuration,workload));
                        break;
                    case "CLICKHOUSE":
                        WorkloadGeneratorEngineThreader.runEngine(workload.getWorkloadThreads(),new ClickhouseWorkloadGeneratorEngine(configuration,workload));
                        break;
                    case "KAFKA":
                        WorkloadGeneratorEngineThreader.runEngine(workload.getWorkloadThreads(),new KafkaWorkloadGeneratorEngine(configuration,workload));
                        break;
                    case "CASSANDRA":
                    case "SNOWFLAKE":
                    case "DB2":
                    case "ORACLE":
                    case "MYSQL":
                    case "POSTGRESQL":
                        System.out.printf("Backend %s not developed yet, exiting.\n", configuration.getBackendType());
                        log.info("Backend {0} not developed yet, exiting.",configuration.getBackendType());
                        System.exit(1);
                        break;
                    default:
                        System.out.println("No Supported Backend Defined See Config YAML.");
                        log.info("Backend {0} not defined see config YAML.",configuration.getBackendType());
                        System.exit(1);

                }
            }
            if (log.isDebugEnabled()) {
                debugConfiguration(configuration);
            }
            log.info("Workloads Started");
            System.out.printf("%d Workloads Started.", configuration.getWorkloads().size());

        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Initialization Error: access to yml is restricted or incorrectly configured");
        }

    }

    private static void debugConfiguration(Configuration configuration) {
        log.debug("Endpoint: " + configuration.getBackendHost() );
        log.debug("Endpoint HTTP Scheme: " + configuration.getBackendScheme() );
        log.debug("Port: " + configuration.getBackendPort() );
        log.debug("ApiKeyID: " + configuration.getBackendApiKeyId());
        log.debug("ApiKeySecret: " + configuration.getBackendApiKeySecret() );
        log.debug("User: " + configuration.getBackendUser() );
        log.debug("Password: " + configuration.getBackendPassword() );
        log.debug("Number of Workloads " + configuration.getWorkloads().size());
    }

    private static Configuration loadConfig(String filename) {
        Configuration configuration = null;
        try {

            File file = new File(filename);
            FileInputStream input = new FileInputStream(file);

            Yaml yaml = new Yaml();
            configuration = yaml.loadAs(input,Configuration.class);

            log.info("Configuration Loaded");
            System.out.println("Configuration Loaded.");

        } catch ( Exception e) {
            System.out.println("Initialization Error: Unable to Load YML model file");
            log.error("Initialization Error: Unable to Load YML model file");
            System.exit(1);
        }
        return configuration;
    }

    private static void validateArguments(String[] args) {
        if (args.length !=1 ) {
            System.out.println("Error - Improper Usage, try: sample-data-generator-all*.jar your_config_file.yml ");
            log.error("Error - Improper Usage, try: sample-data-generator-all*.jar your_config_file.yml ");
            System.exit(1);
        }
    }

}
