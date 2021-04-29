---
layout: default
title: Testnorge Token Provider
parent: Applikasjoner
---

# Testnorge-Token-Provider
Token provider for testnorge. 

## Swagger
Swagger finnes under [/swagger-ui.html](https://testnorge-token-provider.dev.intern.nav.no/swagger-ui.html) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør ApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=local
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
