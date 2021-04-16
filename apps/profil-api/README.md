---
layout: default
title: Brukerprofil API
parent: Applikasjoner
---

# profil-api

Api for henting av profil (navn, e-post og avdeling) og profilbilde for innlogget bruker.

## Swagger
Swagger finnes under [/swagger](https://testnorge-profil-api.dev.adeo.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Kjør ApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[vault-token]
-Dspring.profiles.active=dev
```
