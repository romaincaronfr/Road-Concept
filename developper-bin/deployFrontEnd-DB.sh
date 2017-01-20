#!/bin/bash
../Docker/prepare_build_all.sh
docker rmi docker_frontend;
docker-compose -f ../Docker/Frontend-DB-docker-compose.yml build && docker-compose -f ../Docker/Frontend-DB-docker-compose.yml up;
