# Workload Parameters

### Generic Parameters:

```workloadName``` String that defines a name for a workload<P>
```workloadThreads``` Number of threads that this workload will run in parallel<P>
```workloadSleep``` Number in milliseconds that the engine will pause between sending data to backends<P>
```peakTime``` (OPTIONAL) If filled in the engine will ramp up and down the sleep time to simulate peak times for workloads.<P>
```purgeOnStart``` Boolean that triggers the engine to delete any previous data based on the current workload definition.<P>
```backendBulkQueueDepth``` Number that represents batching of entities being sent form the engine. If set to zero, batching is disabled.<P>

-------------

### Clickhouse Specific Parameters:<p>
```databaseName``` Clickhouse Database<P>
```tableName``` Clickhouse Table<P>
```backendEngine``` You can specify which engine to use, ```MergeTree``` is the one we've used in performance testing.<P>

## Example Clickhouse Workload
```
workloads:
  - workloadName: dda_customer
    databaseName: dda_customer
    tableName: mbb_customer_info
    backendEngine: MergeTree
    workloadThreads: 4
    workloadSleep: 1000
    peakTime: 16:00:00
    purgeOnStart: true
    backendBulkQueueDepth: 50000
    fields:
      - name: ... 

```
-------------
### Elasticsearch Specific Parameters:
```primaryShardCount``` Number of Elasticsearch Primary Shards. Good guideline is to mimic the number of Elasticsearch Nodes you have in your cluster. <P>
```replicaShardCount``` Number of Elasticsearch Secondary or Replica Shards. You can set this to zero if you don't want any replication.<P>
```indexName:``` Elasticsearch Index Name. We'll also automatically create an Elasticsearch Mapping template that emulates your field definitions<P>

## Example Elasticsearch Workload
```
workloads:
  - workloadName: dda_customer
    indexName: mbb_customer_info
    workloadThreads: 4
    workloadSleep: 1000
    primaryShardCount: 3
    replicaShardCount: 0
    peakTime: 09:00:00
    purgeOnStart: true
    backendBulkQueueDepth: 500
    fields:
      - name: ...
```
-------------
### Kafka Specific Parameters:
```topicName``` The target topic that will be created if the topic doesn't exist<P>
```numPartitions```  Number of partitions for a topic <P>
```replicationFactor``` Additional configuration for kafka Topics <P>


## Example Kafka Workload
```
workloads:
  - workloadName: dda_customer
    topicName: SDG
    workloadThreads: 1
    workloadSleep: 1000
    peakTime:
    purgeOnStart: true
    backendBulkQueueDepth: 1000
    numPartitions: 2
    replicationFactor: 2
    fields:
      - name: eai
        type: double
        primary_key: true
        range: 0,3300000
      - name: name
        type: full_name
      - name: location
        type: state
      - name: address
        type: full_address
```
-------------
### RabbitMQ Specific Parameters:
```queueName``` The target topic that will be created if the topic doesn't exist<P>

## Example RabbitMQ Workload
```
workloads:
  - workloadName: dda_customer
    queueName: SDG.EXAMPLE.QUEUE
    workloadThreads: 1
    workloadSleep: 1000
    peakTime:
    purgeOnStart: true
    backendBulkQueueDepth: 0
    fields:
      - name: eai
        type: double
        range: 0,3300000
      - name: name
        type: full_name
      - name: location
        type: state
      - name: address
        type: full_address
```