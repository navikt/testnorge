---
layout: default
title: Bestilling Service
parent: Organisasjon
grand_parent: Applikasjoner
---

## organisajon-bestilling-service
App for å opprette organiasjoner i EREG som lytter fra en kafka kø.


## Swagger
Swagger finnes under [/swagger](https://organisasjon-bestilling-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør ApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
