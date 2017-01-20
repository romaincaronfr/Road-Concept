#!/bin/bash
../Docker/prepare_build_all.sh

docker-compose -f ../Docker/docker-compose-dev.yml build
docker-compose -f ../Docker/docker-compose-dev.yml up

