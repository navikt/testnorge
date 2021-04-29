---
layout: default
title: Person Export API
parent: Applikasjoner
---

# testnorge-person-export-api
Api for å eksportere personer.

## Swagger
Swagger finnes under [/swagger](https://testnorge-person-export-api.dev.adeo.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør PersonExportApiApplicationStarter med følgende argumenter:
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
