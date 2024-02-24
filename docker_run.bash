#!/bin/bash

/app/build_docker_keystore.bash

java -jar simple-data-generator-*-fatJar.jar /config/sdg.yml
