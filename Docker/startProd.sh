#!/usr/bin/env bash

./prepare_build_all.sh 1

#stop running containers
docker-compose --tlsverify --tlscacert=certs/ca.pem --tlscert=certs/cert.pem --tlskey=certs/key.pem -H=roadconcept.4r3.fr:4243 -f docker-compose-prod.yml stop

#build service
docker-compose --tlsverify --tlscacert=certs/ca.pem --tlscert=certs/cert.pem --tlskey=certs/key.pem -H=roadconcept.4r3.fr:4243 -f docker-compose-prod.yml build

#start service
docker-compose --tlsverify --tlscacert=certs/ca.pem --tlscert=certs/cert.pem --tlskey=certs/key.pem -H=roadconcept.4r3.fr:4243 -f docker-compose-prod.yml up -d

