#!/bin/bash
cd ../Road\ Concept\ Backend/;
mvn clean install -DskipTests && cp Road\ Concept\ API/target/road-concept-api-0.1-SNAPSHOT-fat.jar ../Docker/roadconcept;
cd ../Docker/roadconcept;
pwd;
docker-compose -f ../hiveship-docker-compose.yml build && docker-compose -f ../hiveship-docker-compose.yml up &;
sleep(10);
java -jar ../../Road\ Concept\ Backend/Road\ Concept\ API/target/road-concept-api-0.1-SNAPSHOT-fat.jar;
cd ../../developper-bin;
