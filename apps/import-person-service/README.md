---
layout: default title: Import person service parent: Applikasjoner
---

# Testnav-generer-organisasjon-populasjon-service

Service for å opprette liste med identer i pdl forvalter.

## Swagger

Swagger finnes under [/swagger](https://testnav-import-person-service.dev.intern.nav.no/swagger) -endepunktet til
applikasjonen.

## Lokal kjøring

Ha naisdevice kjørende og kjør ImportPersonServiceServiceApplicationStarter med følgende argumenter:

```
-Dspring.cloud.vault.token=[Copy token fra Vault]
-Dspring.profiles.active=dev
```
