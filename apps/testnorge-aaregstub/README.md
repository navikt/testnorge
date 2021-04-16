---
layout: default
title: Testnorge AAreg Stub
parent: Applikasjoner
---

# Testnorge-aaregstub
Testnorge-aaregstub lagrer syntetiserte arbeidsforhold som har blitt sendt/kan sendes til aareg.

## Swagger
Swagger finnes under [/api](https://testnorge-aaregstub.nais.preprod.local/api) -endepunktet til applikasjonen.

## Lokal kjøring
Kjør ApplicationStarter med følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
-Dspring.profiles.active=dev
```
