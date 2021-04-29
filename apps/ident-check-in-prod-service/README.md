---
layout: default
title: Finnes ident i prod service
parent: Applikasjoner
---

# ident-check-in-prod-service
App som sjekker om de gitte identene finnes i prod. Blir benyttet av Dolly og Orkestratoren.

## Swagger
Swagger finnes under [/swagger](https://testnorge-ident-check-in-prod-service.dev.adeo.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør IdentServiceApplicationStarter med følgende argumenter:
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
