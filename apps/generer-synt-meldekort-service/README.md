---
layout: default
title: Generer Synt Meldekort Service
parent: Applikasjoner
---

# Testnav-generer-synt-meldkort-service
App for å generere syntetiske meldekort for Arena.

## Swagger
Swagger finnes under [/swagger](https://testnav-generer-synt-meldekort-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør GenererSyntMeldekortApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[vault-token]
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
