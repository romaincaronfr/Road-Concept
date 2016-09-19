#!/bin/bash

cd core;
mvn clean install;
cd ..;

cd api;
mvn clean install;
cd ..;


