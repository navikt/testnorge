---
layout: default
title: Testnorge Spion
parent: Applikasjoner
---

# testnorge-spion
Testnorge-spion tilbyr endepunkt for å lage syntetiske vedtak og legge dem på Kafka kø til SPION.

## Dokumentasjon

### Swagger
Swagger finnes under [/api](https://testnorge-spion.dev.intern.nav.no/api) -endepunktet til applikasjonen.

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