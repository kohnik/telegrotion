#!/bin/bash

#Build backend module
mvn clean install

# Build Docker image
docker build --platform linux/amd64 -t alexanderrybak/fizzly-app:version-1 .

# Tag image for Heroku
docker tag alexanderrybak/fizzly-app:version-1 registry.heroku.com/fizzly/web

# Push to Heroku
heroku container:push web -a fizzly

# Release on Heroku
heroku container:release web -a fizzly