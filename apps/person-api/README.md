---
layout: default
title: Person API
parent: Applikasjoner
---

# Testnorge-Person-api
API for person.

## Swagger
Swagger finnes under [/swagger](https://testnorge-person-api.nais.preprod.local/swagger) -endepunktet til applikasjonen.
 
## Lokal kjøring
Ha naisdevice kjørende og kjør PersonApiApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[Copy token fra Vault]
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må i tillegg ha følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
