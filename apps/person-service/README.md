---
layout: default
title: Testnav Person Service
parent: Applikasjoner
---

# Testnav-Person-Service
API for oppretting og henting av personer fra PDL og TPS

## Swagger
Swagger finnes under [/swagger-ui.html](https://testnav-person-service.intern.dev.nav.no/swagger-ui.html) -endepunktet til applikasjonen.
 
## Lokal kjøring
Ha naisdevice kjørende og kjør PersonServiceApplicationStarter med følgende argumenter:
```
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
