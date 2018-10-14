#!/bin/sh


BASEDIR=$(dirname $0)
cd ${BASEDIR}/..


npm run build
docker build -f ./docker/Dockerfile . -t devplant/chat-frontend
docker push devplant/chat-frontend