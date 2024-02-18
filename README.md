# Simple Data Generator for BigData
The purpose of this project is to ingest some sample data into SQL and NON-SQL products like Elasitsearch, Clickhouse and others. 
If you have a model in mind, but aren't ready to build piplines or deploy write code to index/insert data, this project might help you.

It's multithreaded engine so you can generate a fair amount of load.
It's a refactor of ajpahl1008/sample-data-generator eliminating code for each new workload.
It's completely YAML driven. Yay Yaml!!!

Advice: Although this code is scalable and multithreaded, it's primary purpose is to generate data not a benchmarking tool. Yet. 

## Different Ways to Get Started

### Build/Run

If you want to build this code, you'll need the following:
* Gradle (8.5)
* Java OpenJDK (17+)

To compile: <P>
```# gradle clean; gradle build fatJar``` <P>
To Run: <P>
```# java -jar build/libs/simple-data-generator-*-fatJar.jar config.yml```

## Just Run
Get a release off our [releases](https://github.com/ajpahl1008/simple-data-generator/releases) in GitHub
OR
Use the [container](https://hub.docker.com/r/ajpahl1008/simple-data-generator) from DockerHub.

```docker pull ajpahl1008/simple-data-generator``` <p>
```docker run -v ./your_config.yml:/config/sdg.yml ajpahl1008/simple-data-generator:3.1.0```


## Step 1: Create a Keystore for TLS 

If you're using a SaaS offering to store your data, it's probably using TLS.  
When necessary, we'll update this script so you can create that keystore.  
Currently, we only need it for connecting to Elasticsearch clusters.   
_Remember, none of this is necessary if you use the containerized version where we take care if it automagically_.

**For Elasticsearch:**
```
# ./build_keystore.bash <keystore_password> <elasticsearch_host> <elasticsearch_port>
```
**Arguments:** 
  * keystore_password: something you arbitrarily set when you create they keystore for the first time.
  * elasticsearch_host: exclude the http/https it's just the FQDN that resolves to your cluster.
  * elasticsearch_port: whatever port you've specified for Elasticsearch.  If you're on Elasticsearch Service (cloud.elastic.co) it's 9243.

## Step 2: Create A configuration YAML (yml) file.

There's a couple of examples in the example directory but here's the basic structure.
```
backendType: <ELASTICSEARCH or CLICKHOUSE>
backendScheme: https
backendHost: <REQUIRED>
backendPort: 9243
backendUser: 
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
    elasticsearchBulkQueueDepth: 0
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
```
backendType: <ELASTICSEARCH or CLICKHOUSE>
backendScheme: https
backendHost: <REQUIRED>
backendPort: 9243
backendUser: 
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
    elasticsearchBulkQueueDepth: 0
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
     elasticsearchBulkQueueDepth: 0
    fields:
      - name: inventory_part_number
        type: int
   ...
```
Documentation on the different types: https://github.com/ajpahl1008/simple-data-generator/blob/master/docs/supported_fields.md 


## Running with Elastic Application Performance Monitoring (APM)
```
complete the <NEED_THIS> fields in the runme_apm.bash script
Simply, you need the URL for your APM server and the token provided by APM.

Additionally, there's a debug script runme_apm_debug.bash if things get confusing or not going smoothly.
```
