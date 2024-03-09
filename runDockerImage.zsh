#!/bin/zsh

if [ -z "$1" ]; then
  echo "Error: Not enough arguments"
  echo "Usage: runDockerImage.zsh <version> <config_file>"
  exit 1;
fi

if [ -z "$2" ]; then
  echo "Error: Not enough arguments"
  echo "Usage: runDockerImage.zsh <version> <config_file>"
  exit 1;
fi

echo "Running bespinengineering/simple-data-generator:${1} "
docker run -v ${2}:/config/sdg.yml bespinengineering/simple-data-generator:${1}
