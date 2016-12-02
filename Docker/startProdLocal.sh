#!/usr/bin/env bash

rm -rf ./nginx/frontend
cp -r ../frontend/ ./nginx/frontend/

rm ./nginx/frontend/js/app.js.bak

cd ../Road\ Concept\ Backend/ && mvn clean install && cp Road\ Concept\ API/target/road-concept-api-0.1-SNAPSHOT-fat.jar ../Docker/roadconcept && cd ../Docker

#stop running containers
docker-compose -f docker-compose-prod.yml stop

#build service
docker-compose -f docker-compose-prod.yml build

#start service
docker-compose -f docker-compose-prod.yml up

