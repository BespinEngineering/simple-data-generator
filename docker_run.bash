#!/bin/bash

/app/build_docker_keystore.bash

java -jar simple-data-generator-3.0.0-SNAPSHOT-fatJar.jar /config/sdg.yml
