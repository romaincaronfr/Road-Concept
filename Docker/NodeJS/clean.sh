#!/usr/bin/env bash

SCRIPTPATH=$( cd $(dirname $0) ; pwd -P )
cd $SCRIPTPATH

rm ./buildData/*.js
rm ./buildData/package.json

