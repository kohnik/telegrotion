#!/bin/bash

# Build websocket module
mvn clean install

# Build Docker image
docker build --platform linux/amd64 -t alexanderrybak/fizzly-app-websocket:version-1 .

# Tag image for Heroku
docker tag alexanderrybak/fizzly-app-websocket:version-1 registry.heroku.com/fizzly-websocket/web

# Push to Heroku
heroku container:push web -a fizzly-websocket

# Release on Heroku
heroku container:release web -a fizzly-websocket