---
layout: default title: Organisasjon tilgang service parent: Organisasjon grand_parent: Applikasjoner
---

## organisajon-tilgang-service

Service som godkjenner tilganger for en spesifisert organisasjoner mot Dolly ved bruk av bankid.

## Swagger

Swagger finnes under [/swagger](https://testnav-organisasjon-tilgang-service.dev.intern.nav.no/swagger) -endepunktet til
applikasjonen.

## Lokal kjøring

Ha naisdevice kjørende og kjør OrganisasjonTilgangServiceApplicationStarter med følgende argumenter:

``` 
-Dspring.profiles.active=local
-Dspring.cloud.vault.token=[vault-token]
```
