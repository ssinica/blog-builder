#!/bin/sh

set -e

IMAGE_NAME=$1
IMAGE_TAG=$2
CONTAINER_NAME=$3
NAMESPACE=$4

echo **************************************
echo IMAGE_NAME = ${IMAGE_NAME}
echo IMAGE_TAG = ${IMAGE_TAG}
echo CONTAINER_NAME = ${CONTAINER_NAME}
echo NAMESPACE = ${NAMESPACE}
echo **************************************

echo Pulling image ${NAMESPACE}/${IMAGE_NAME}:${IMAGE_TAG} ...
docker pull ${NAMESPACE}/${IMAGE_NAME}:${IMAGE_TAG}

CID_RUNNING=$(docker ps | grep "${CONTAINER_NAME}" | awk '{print $1}')
if [ -n "${CID_RUNNING}" ]; then
    echo Stopping old container ${CID_RUNNING}...
    docker stop ${CID_RUNNING}
fi

CID=$(docker ps -a | grep "${CONTAINER_NAME}" | awk '{print $1}')
if [ -n "${CID}" ]; then
    echo Removing old container ${CID}...
    docker rm -fv ${CID}
fi
