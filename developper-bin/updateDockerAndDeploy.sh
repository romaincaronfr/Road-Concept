#!/bin/bash
cp -R ../frontend ../Docker/nginx && cd ../Road\ Concept\ Backend/ && mvn clean install && cp Road\ Concept\ API/target/road-concept-api-1.0-RELEASE-fat.jar ../Docker/roadconcept && cd ../Docker/roadconcept && docker-compose -f ../docker-compose-dev.yml build && docker-compose -f ../docker-compose-dev.yml up;

