---
layout: default
title: Synt Person API
parent: Applikasjoner
---

## Testnorge-Synt-person-api
API for syntetisering av personer

### Swagger
Swagger finnes under [/swagger](https://testnorge-synt-person-api.dev.adeo.no/swagger) -endepunktet til applikasjonen.

### Lokal kjøring
Ha naisdevice kjørende og kjør SyntSykemeldingApiApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
    
