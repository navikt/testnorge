---
layout: default
title: Testnorge AAreg Stub
parent: Applikasjoner
---

# Testnorge-aaregstub
Testnorge-aaregstub lagrer syntetiserte arbeidsforhold som har blitt sendt/kan sendes til aareg.

## Swagger
Swagger finnes under [/api](https://testnorge-aaregstub.dev.intern.nav.no/api) -endepunktet til applikasjonen.

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
    

