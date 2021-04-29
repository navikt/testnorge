---
layout: default
title: Testnorge Frikort
parent: Applikasjoner
---

# Testnorge-frikort
Testnorge-frikort tilbyr endepunkt for å lage syntetiske egenandelsmeldinger.

## Swagger
Swagger finnes under [/api](https://testnorge-frikort.dev.intern.nav.no/api) -endepunktet til applikasjonen.

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
