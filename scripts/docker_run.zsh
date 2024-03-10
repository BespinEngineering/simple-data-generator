#!/bin/zsh

/app/build_docker_keystore.zsh

java -jar simple-data-generator-*-fatJar.jar /config/sdg.yml
