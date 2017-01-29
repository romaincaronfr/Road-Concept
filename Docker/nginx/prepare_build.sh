#!/usr/bin/env bash

SCRIPTPATH=$( cd $(dirname $0) ; pwd -P )
cd $SCRIPTPATH

if [ $# -lt 1 ]
then
    echo "missing argument, autoset to local"
    set "0"
fi

cp -r ../../Road\ Concept\ Frontend/ ./buildData/frontend/
if [ $1 -ne 0 ]
then
    sed -ri.bak 's/(http:\/\/localhost:8080)/http:\/\/roadconcept\.4r3\.fr:8081/' ./buildData/frontend/js/app.js
    rm ./buildData/frontend/js/app.js.bak
fi
