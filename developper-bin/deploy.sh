#!/bin/bash
cd ../Road\ Concept\ Backend/;
cd ../Docker/roadconcept;
docker-compose -f ../hiveship-docker-compose.yml build && docker-compose -f ../hiveship-docker-compose.yml up;
cd ../../developper-bin;
