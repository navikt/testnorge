#!/usr/bin/env sh

#
# Converts NAIS provided key.pem to PKCS#8 PEM format, which can be used by R2dbc.
#
openssl pkey -in /var/run/secrets/nais.io/sqlcertificate/key.pem -out /tmp/pk8.pem