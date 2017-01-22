#!/usr/bin/env bash
SCRIPTPATH=$( cd $(dirname $0) ; pwd -P )

cd $SCRIPTPATH
cd ../../Road\ Concept\ Backend/ && mvn clean install
cp Road\ Concept\ API/target/*fat.jar ../Docker/roadconcept/buildData/road-concept.jar