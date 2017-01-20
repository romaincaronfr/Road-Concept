#!/usr/bin/env bash

if [ $# -lt 1 ]
then
    echo "missing argument, autoset to local"
    set "0"
fi

if [ $1 -eq 0 ]
then
    echo "local mode"
else
    echo "server mode"
fi

SCRIPTPATH=$( cd $(dirname $0) ; pwd -P )

cd $SCRIPTPATH

./clean_all.sh
./roadconcept/prepare_build.sh
./nginx/prepare_build.sh $0
./NodeJS/prepare_build.sh