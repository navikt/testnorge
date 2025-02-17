#!/bin/bash

SERVICEUSER_USERNAME=$(cat /var/run/secrets/nais.io/serviceuser/password)
export SERVICEUSER_USERNAME

SERVICEUSER_PASSWORD=$(cat /secret/serviceuser/password)
export SERVICEUSER_PASSWORD