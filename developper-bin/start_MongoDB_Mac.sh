#!/bin/bash

if [ ! -d "./MongoDB/Mac/data" ]; then
  mkdir ../BDD/MongoDB/Mac/data
fi

if [ ! -d "./MongoDB/Mac/data/db" ]; then
  mkdir ../BDD/MongoDB/Mac/data/db
fi

../BDD/MongoDB/Mac/binMongo/mongod --dbpath ../BDD/MongoDB/Mac/data/db
