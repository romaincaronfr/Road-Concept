#!/bin/bash
cd ../Road\ Concept\ Backend/;
mvn clean install -DskipTests && cp Road\ Concept\ API/target/road-concept-api-0.1-SNAPSHOT-fat.jar ../Docker/roadconcept;
cd ../Docker/roadconcept;
docker-compose build && docker-compose up;
cd ../../developper-bin;
