#!/bin/bash
cp -R ../frontend ../Docker/nginx && cd ../Road\ Concept\ Backend/ && mvn clean install && cp Road\ Concept\ API/target/*fat.jar ../Docker/roadconcept/road-concept.jar && cd ../Docker/roadconcept && docker-compose -f ../docker-compose-dev.yml build && docker-compose -f ../docker-compose-dev.yml up;

