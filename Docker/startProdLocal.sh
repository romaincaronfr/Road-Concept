#!/usr/bin/env bash

rm -rf ./nginx/frontend
cp -r ../frontend/ ./nginx/frontend/

rm ./nginx/frontend/js/app.js.bak

cd ../Road\ Concept\ Backend/ && mvn clean install && cp Road\ Concept\ API/target/*fat.jar ../Docker/roadconcept/road-concept.jar && cd ../Docker

#stop running containers
docker-compose -f docker-compose-prod.yml stop

#build service
docker-compose -f docker-compose-prod.yml build

#start service
docker-compose -f docker-compose-prod.yml up

