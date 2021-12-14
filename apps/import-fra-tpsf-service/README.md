---
layout: default
title: Import fra TPSF service
parent: Applikasjoner
---

# Import-fra-TPSF-service
Import-fra-TPSF-service leser SKD-meldinger fra gruppe i TPS-forvalteren og skriver til PDL-forvalteren + PDL
 
## Swagger
Swagger finnes under [/api](https://import-fra-tpsf-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør ImportFraTpsfServiceApplicationStarter med følgende argumenter:
```
--add-opens java.base/java.lang=ALL-UNNAMED
-Dspring.profiles.active=local
-Dspring.cloud.vault.token=[kopier token fra vault]
```

