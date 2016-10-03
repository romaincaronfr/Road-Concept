#!/bin/bash
cd ../Road\ Concept\ Backend/;
mvn clean install -DskipTests && java -jar Road\ Concept\ API/target/road-concept-api-0.1-SNAPSHOT-fat.jar;
cd ../developper-bin;
