#!/bin/sh

apt-get update && apt-get install -y \
   openjdk-7-jdk \
   maven \
   git \
   wget \
   nano

echo "DOCKER_OPTS='-r=true -H tcp://0.0.0.0:4243 -H unix:///var/run/docker.sock'" > /etc/default/docker

service docker restart
