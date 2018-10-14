#!/bin/sh


BASEDIR=$(dirname $0)
cd ${BASEDIR}
docker build -f ./Dockerfile . -t devplant/chat-keycloak
docker push devplant/chat-keycloak