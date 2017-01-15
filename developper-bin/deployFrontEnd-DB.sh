#!/bin/bash
docker ps -a | awk '{ print $1,$2 }' | grep docker_frontend | awk '{print $1 }' | xargs -I {} docker rm {};
docker rmi docker_frontend;
cp -R ../frontend ../Docker/nginx;
cd ../Road\ Concept\ Backend/;
cd ../Docker/roadconcept;
docker-compose -f ../hiveship-docker-compose.yml build && docker-compose -f ../frontend-DB-docker-compose.yml up;
cd ../../developper-bin;
