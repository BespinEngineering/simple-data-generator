# Configuration Parameters

### Generic Parameters:
```backendType``` What technology are we sending data to? CLICKHOUSE or ELASTICSEARCH<P>
```backendScheme``` http or https scheme for communication<P>
```backendHost``` FQDN for host (or IP Address)<P>
```backendPort``` The network port your backend is listening on.<P>
```backendUser``` User ID for target.<P>
```backendPassword``` Password<P>
```backendApiKeyEnabled``` Indicates if the target tech has API Keys enabled.  This currently only works with Elasticsearch. If not used leave empty.<P>
```backendApiKeyId```  API Key ID.  If not set, leave empty.<P>
```backendApiSecretKey``` API Key Secret, if not set, leave empty<P>
```keystoreLocation``` Typically ```keystore.jks``` but you can change this.
```keystorePassword``` Password for accessing your Java Keystore (JKS) 

## Example Clickhouse Configuration
```
 backendType: CLICKHOUSE
backendScheme: https
backendHost: hostname.dns.com
backendPort: 8123
backendUser: default
backendPassword: password
backendApiKeyEnabled: false
backendApiKeyId:
backendApiKeySecret:
keystoreLocation:
keystorePassword:
workloads:
  - workloadName: dda_customer
    databaseName: dda_customer
    tableName: mbb_customer_info
    backendEngine: MergeTree
    workloadThreads: 8
    workloadSleep: 10
    primaryShardCount: 3
    replicaShardCount: 0
    peakTime:
    purgeOnStart: true
    backendBulkQueueDepth: 50000
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