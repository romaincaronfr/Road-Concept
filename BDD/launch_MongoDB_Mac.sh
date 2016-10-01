#!/bin/bash

if [ ! -d "./MongoDB/Mac/data" ]; then
  mkdir MongoDB/Mac/data
fi

if [ ! -d "./MongoDB/Mac/data/db" ]; then
  mkdir MongoDB/Mac/data/db
fi

./MongoDB/Mac/binMongo/mongod --dbpath MongoDB/Mac/data/db
