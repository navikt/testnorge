---
layout: default
title: Service
parent: Endringsmelding
grand_parent: Applikasjoner
---

# Endringsmelding-service

## Swagger
Swagger finnes under [/swagger](https://endringsmelding-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.
For å kunne bruke endringsmelding-endepunktet må det innsendte tokenet ha tilgang via azure applikasjonen `dev-gcp:dolly:endringsmelding-frontend`.
 
## Lokal kjøring
  
### Utviklerimage
Kjør ApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[Copy token fra Vault]
-Dspring.profiles.active=dev
```
