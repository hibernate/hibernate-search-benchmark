#!/usr/bin/env bash
# inspired by https://github.com/quarkusio/quarkus .github/docker-prune.sh

set -e

echo 'Container list before prune:'
docker ps -as

docker container prune -f
docker image prune -f
docker network prune -f
docker volume prune -f

echo 'Container list after prune:'
docker ps -as