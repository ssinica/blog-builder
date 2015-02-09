#!/bin/bash
set -e

echo "Welcome to Blog Builder!"

chown -R blog /app/config
chown -R blog /app/out
chown -R blog /app/lib

chmod -R +r /app/in

if [ ! -z $1 ]; then
	exec gosu blog java -jar /app/lib/server.jar "$@"
else
    exec "$@"
fi