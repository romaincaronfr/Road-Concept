#!/usr/bin/env bash

rm -rf ./nginx/frontend
cp -r ../frontend/ ./nginx/frontend/

sed -ri.bak 's/(http:\/\/localhost:8080)/http:\/\/roadconcept\.4r3\.fr:8081/' ./nginx/frontend/js/app.js
rm ./nginx/frontend/js/app.js.bak

cd ../Road\ Concept\ Backend/ && mvn clean install -DskipTests && cp Road\ Concept\ API/target/road-concept-api-0.1-SNAPSHOT-fat.jar ../Docker/roadconcept && cd ../Docker

#stop running containers
docker-compose --tlsverify --tlscacert=certs/ca.pem --tlscert=certs/cert.pem --tlskey=certs/key.pem -H=roadconcept.4r3.fr:4243 -f docker-compose-prod.yml stop

#build service
docker-compose --tlsverify --tlscacert=certs/ca.pem --tlscert=certs/cert.pem --tlskey=certs/key.pem -H=roadconcept.4r3.fr:4243 -f docker-compose-prod.yml build

#start service
docker-compose --tlsverify --tlscacert=certs/ca.pem --tlscert=certs/cert.pem --tlskey=certs/key.pem -H=roadconcept.4r3.fr:4243 -f docker-compose-prod.yml up -d

