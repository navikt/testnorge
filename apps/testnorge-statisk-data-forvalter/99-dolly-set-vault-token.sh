#!/usr/bin/env sh

#
# Load the contents of the NAIS provided vault token into a Spring Boot friendly environment variable.
#
SPRING_CLOUD_VAULT_TOKEN=$(cat /var/run/secrets/nais.io/vault/vault_token)
export SPRING_CLOUD_VAULT_TOKEN
