#!/usr/bin/env bash

rm -rf ./nginx/frontend
cp -r ../frontend/ ./nginx/frontend/

while((1))
do
    echo -n "entrez l'addresse de votre serveur : "
    read addresse
    [ ! -z $addresse ] && break
    echo "ce champ est obligatoire."
done

while((1))
do
    echo -n "entrez le nom de votre logFile : "
    read logFile
    [ ! -z $logFile ] && break
    echo "ce champ est obligatoire."
done

sed -ri.bak "s/(localhost:8080)/$addresse:8080/" ./nginx/frontend/js/app.js
rm ./nginx/frontend/js/app.js.bak

#stop running containers
docker-compose -f docker-compose-prod.yml stop

#build service
docker-compose -f docker-compose-prod.yml build

#start service
docker-compose -f docker-compose-prod.yml up -d

docker-compose -f docker-compose-prod.yml logs -f -t --no-color >> $logFile
