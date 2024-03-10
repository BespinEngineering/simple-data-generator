# Simple Data Generator for BigData
The purpose of this project is to ingest some sample data into SQL and NON-SQL products like Elasticsearch, Clickhouse, Kafka and others. 
If you have a model in mind, but aren't ready to build pipelines or deploy write code to index/insert data, this project might help you.

The engine is multithreaded, so you can generate a fair amount of load.
It's completely YAML driven. Yay Yaml!!!

Advice: Although this code is scalable and multithreaded, it's primary purpose is to generate data not a benchmarking tool. Yet. 

## <U> Two Options for Running Simple Data Generator</U>

## 1 - Build & Run

If you want to build this code, you'll need the following:
* Gradle (8.5)
* Java OpenJDK (17+)

To compile: <P>
```# gradle clean; gradle build fatJar``` <P>
To Run: <P>
```# java -jar build/libs/simple-data-generator-*-fatJar.jar ./your_config.yml```

## 2 - Just Run
Get a release off our [releases](https://github.com/bespinengineering/simple-data-generator/releases) in GitHub

See setup steps below for TLS and Building your config, then Run: <P>
```# java -jar build/libs/simple-data-generator-*-fatJar.jar ./your_config.yml```

OR
Use the [container](https://hub.docker.com/r/bespinengineering/simple-data-generator) from DockerHub.

```docker pull bespinengineering/simple-data-generator``` <p>
Build your config (see Step 2 below)<p>
```docker run -v ./your_config.yml:/config/sdg.yml bespinengineering/simple-data-generator:{{RELEASE_VERSION}}```


## Setup Step 1: Create a Keystore for TLS 

_<B>Note: this step isn't necessary if you use the containerized version where we take care if it automagically_.</B>

If you're using a SaaS offering to store your data, it's probably using TLS.  
When necessary, we'll update this script so you can create that keystore.  
Currently, we only need it for connecting to Elasticsearch clusters.   

**For Elasticsearch Use:**
```
# ./build_keystore.bash <keystore_password> <elasticsearch_host> <elasticsearch_port>
```
**Arguments:** 
  * ```keystore_password``` something you arbitrarily set when you create they keystore for the first time.
  * ```elasticsearch_host``` exclude the http/https it's just the FQDN that resolves to your cluster.
  * ```elasticsearch_port``` whatever TLS port you've configured for Elasticsearch.  If you're on Elasticsearch Service (cloud.elastic.co) it's 9243.


## Setup Step 2: Create A configuration YAML (yml) file.

##### See more detailed documentation on Configs, Workloads and Fields
Configuration Parameters [Documentation](https://github.com/bespinengineering/simple-data-generator/blob/master/docs/configuration_parameters.md) 

Workload Parameters [Documentation](https://github.com/bespinengineering/simple-data-generator/blob/master/docs/workload_parameters.md) 

Supported Field Parameters [Documentation](https://github.com/bespinengineering/simple-data-generator/blob/master/docs/supported_fields.md) 



There are few configuration examples in the ./examples directory in this repo, but here is the basic structure.
Fields listed as <OPTIONAL> can be left blank or omitted altogether. 
```
backendType: <ELASTICSEARCH or CLICKHOUSE>
backendScheme: <https or https>
backendHost: <REQUIRED>
backendPort: <REQUIRED>
backendUser: <REQUIRED>
backendPassword: <REQUIRED>
backendApiKeyEnabled: false
backendApiKeyId: <OPTIONAL>
backendApiKeySecret: <OPTIONAL>
keystoreLocation: keystore.jks
keystorePassword: <REQUIRED>
workloads:
  - workloadName: workload_1
    indexName: index-1    
    workloadThreads: 1
    workloadSleep: 250
    primaryShardCount: 3  #Elasticsearch Specific
    replicaShardCount: 0  #Elasticsearch Specific
    peakTime: <OPTIONAL>
    purgeOnStart: true
    backendBulkQueueDepth: 0
    fields:
      - name: account_number
        type: int
      - name: state
        type: state
      - name: balance
        type: float
        range: 1,20000

```
### Multiple Workload Structure
Depending on resource availability, you can have multiple workloads within one configuration.

```
backendType: <ELASTICSEARCH or CLICKHOUSE>
backendScheme: <https or https>
backendHost: <REQUIRED>
backendPort: 9243
backendUser: <REQUIRED>
backendPassword: <REQUIRED>
backendApiKeyEnabled: false
backendApiKeyId: <OPTIONAL>
backendApiKeySecret: <OPTIONAL>
keystoreLocation: keystore.jks
keystorePassword: <REQUIRED>
workloads:
  - workloadName: workload_1
    indexName: index-1
    workloadThreads: 1
    workloadSleep: 250
    primaryShardCount: 3
    replicaShardCount: 0
    peakTime: 19:00:00
    purgeOnStart: true
    backendBulkQueueDepth: 0
    fields:
      - name: account_number
        type: int
   ...
   - workloadName: workload_2
     indexName: index-2
     workloadThreads: 1
     workloadSleep: 250
     primaryShardCount: 3
     replicaShardCount: 0
     peakTime: 19:00:00
     purgeOnStart: true
     backendBulkQueueDepth: 0
    fields:
      - name: inventory_part_number
        type: int
   ...
```
