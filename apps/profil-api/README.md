---
layout: default
title: Brukerprofil API
parent: Applikasjoner
---

# profil-api

Api for henting av profil (navn, e-post og avdeling) og profilbilde for innlogget bruker.

## Swagger
Swagger finnes under [/swagger](https://testnorge-profil-api.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør ProfilApiApplicationStarter med følgende argumenter:
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
