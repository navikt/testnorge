---
layout: default title: Organisasjon mottak service parent: Organisasjon grand_parent: Applikasjoner
---

## organisajon-mottak-service

App for å opprette organisasjoner i EREG som lytter fra en kafka kø.

## Swagger

Swagger finnes under [/swagger](https://testnav-organisasjon-mottak-service.dev.intern.nav.no/swagger) -endepunktet til
applikasjonen.

## Lokal kjøring

Ha naisdevice kjørende og kjør OMSApplicationStarter med følgende argumenter:

``` 
-Dspring.profiles.active=dev
-Dspring.cloud.vault.token=[vault-token]
```
