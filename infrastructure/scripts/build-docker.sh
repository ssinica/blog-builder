#!/bin/sh
cd /data/project
DOCKER_HOST=http://localhost:4243 mvn clean install docker:build