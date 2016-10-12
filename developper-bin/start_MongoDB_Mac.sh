#!/bin/bash

if [ ! -d "./MongoDB/Mac/data" ]; then
  mkdir ../DB/MongoDB/Mac/data
fi

if [ ! -d "./MongoDB/Mac/data/db" ]; then
  mkdir ../DB/MongoDB/Mac/data/db
fi

../DB/MongoDB/Mac/binMongo/mongod --dbpath ../DB/MongoDB/Mac/data/db
