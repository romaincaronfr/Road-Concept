#!bin/bash

service mongod start
sleep 15
java -jar /var/road-concept-api-0.1-SNAPSHOT-fat.jar