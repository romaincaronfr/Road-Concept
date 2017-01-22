#!/usr/bin/env bash

SCRIPTPATH=$( cd $(dirname $0) ; pwd -P )
echo $SCRIPTPATH

$SCRIPTPATH/roadconcept/clean.sh
$SCRIPTPATH/nginx/clean.sh