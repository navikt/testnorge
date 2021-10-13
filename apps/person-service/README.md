---
layout: default
title: Person Service
parent: Applikasjoner
---

# Testnorge-Person-api
API for oppretting og henting av personer fra PDL og TPS

## Swagger
Swagger finnes under [/swagger-ui.html](https://testnav-person-service.dev.intern.nav.no/swagger-ui.html) -endepunktet til applikasjonen.
 
## Lokal kjøring
Ha naisdevice kjørende og kjør PersonServiceApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[Copy token fra Vault]
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
