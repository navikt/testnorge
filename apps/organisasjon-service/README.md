---
layout: default
title: Service
parent: Organisasjon
grand_parent: Applikasjoner
---

## Organisasjon-api
API for organsiasjoner.

### Swagger
Swagger finnes under [/swagger](https://testnav-organisasjon-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

### Lokal kjøring
Ha naisdevice kjørende og kjør OrganisasjonApiApplicationStarter med følgende argumenter:
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
