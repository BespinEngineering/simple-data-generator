#!/bin/bash
PASSWORD="`cat /config/sdg.yml | grep keystorePassword | awk -F\: '{print $2}'| tr -d '[:blank:]'`"

HOST="`cat /config/sdg.yml | grep elasticsearchHost | awk -F\: '{printf $2}' | tr -d '[:blank:]'`"
PORT="`cat /config/sdg.yml | grep elasticsearchPort | awk -F\: '{printf $2}' | tr -d '[:blank:]'`"

openssl s_client -connect ${HOST}:${PORT} </dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > /app/es_cloud.cert

keytool -genkey -alias elastic_servers \
    -keyalg RSA -keystore /app/keystore.jks \
    -dname "CN=DockerPerson, OU=DockerImageMakers, O=Persons L=Atlanta, S=Georgia, C=US" \
    -storepass ${PASSWORD} -keypass ${PASSWORD}

keytool -import -noprompt -trustcacerts -alias elastic_cloud -file es_cloud.cert \
    -keystore ./keystore.jks -storepass ${PASSWORD}
