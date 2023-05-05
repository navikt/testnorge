---
layout: default
title: Varslinger service
parent: Applikasjoner
---

# Varslinger-service
Applikasjon for registering av varslinger og brukeres varslinger. 

## Swagger
Swagger finnes under [/swagger](https://testnav-varslinger-service.intern.dev.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør deretter VarslingerServiceApplicationStarter med følgende argumenter:
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
