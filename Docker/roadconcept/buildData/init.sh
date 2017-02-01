#!bin/bash

sleep 20
java -jar -Xms2g -Xmx6g -Dvertx.options.maxEventLoopExecuteTime=100000000000 /var/road-concept.jar
