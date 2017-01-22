#!/usr/bin/env bash

./prepare_build_all.sh 0

#stop running containers
docker-compose -f docker-compose-prod.yml stop

#build service
docker-compose -f docker-compose-prod.yml build

#start service
docker-compose -f docker-compose-prod.yml up

