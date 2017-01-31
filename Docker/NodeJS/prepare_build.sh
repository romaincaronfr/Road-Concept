#!/usr/bin/env bash
SCRIPTPATH=$( cd $(dirname $0) ; pwd -P )
cd $SCRIPTPATH

cp ../../Road\ Concept\ NodeJS/main.js ./buildData/main.js
cp ../../Road\ Concept\ NodeJS/package.json ./buildData/package.json