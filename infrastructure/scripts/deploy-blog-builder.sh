#!/bin/sh

set -e

IMAGE_TAG=${1-latest}
IMAGE_NAME="blog-builder"
CONTAINER_NAME="blog-builder"
NAMESPACE="ssinica"

#/data/scripts/prepare-deploy-docker-container.sh ${IMAGE_NAME} ${IMAGE_TAG} ${CONTAINER_NAME} ${NAMESPACE}

docker run -ti \
 -p 80:8080 \
 -v /data/blog/config:/app/config \
 -v /data/blog/source:/app/in \
 -v /data/blog/result:/app/out \
 --name=${CONTAINER_NAME} \
 --rm \
 ${NAMESPACE}/${IMAGE_NAME}:${IMAGE_TAG} \
 /app/config/blog.properties