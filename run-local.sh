#!/usr/bin/env bash

# Linux warning
platform() {
 [[ $(uname) == 'Linux' ]] && echo "linux" || echo "mac-or-windows"
}

if [[ "$(platform)" == 'linux' ]]; then
  compose_command=docker-compose
else
  compose_command=podman-compose
fi

if [[ $* == *--cleanup* ]]; then
   echo "Cleaning Database Container..."
   $compose_command -f ./docker-compose.yml down
   exit 0
fi

echo "Starting Database Container..."
$compose_command -f ./docker-compose.yml --build -d

echo "Running Authz/Authn Service..."
./gradlew bootRun
