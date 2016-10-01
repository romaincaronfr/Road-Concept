#!/bin/bash

cd core;
mvn clean install;
cd ..;

cd api;
mvn clean install;
java -jar target/road-concept-api-0.1-SNAPSHOT-fat.jar 
cd ..;
