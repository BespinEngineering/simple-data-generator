#!/bin/zsh

if [ -z "$1" ]; then
  echo "Error: Not enough arguments"
  echo "Usage: createDockerImage.zsh <version>"
  exit 1;
fi

docker buildx build --push --platform linux/arm64,linux/amd64 --no-cache --provenance=true --sbom=true \
--tag bespinengineering/simple-data-generator:$1 .

echo "Build Complete: bespinengineering/simple-data-generator:${1} "