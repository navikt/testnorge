---
layout: default title: Person organisasjon tilgang service parent: Applikasjoner
---

# Person Organisasjon Tilgang Service

Service som henter organisasjoner fra Altinn og hvilke tilganger de har

## Swagger

Swagger finnes under [/swagger](https://testnav-person-organisasjon-tilgang-service.dev.intern.nav.no/swagger)
-endepunktet til applikasjonen.

## Lokal kjøring

Ha naisdevice kjørende og kjør PersonOrganisasjonTilgangServiceApplicationStarter med følgende argumenter:

``` 
-Dspring.profiles.active=local
-Dspring.cloud.vault.token=[vault-token]
```
