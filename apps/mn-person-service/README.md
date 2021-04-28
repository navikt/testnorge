---
layout: default
title: Person Service
parent: Mini Norge
grand_parent: Applikasjoner
---

# Mini-Norge Person Service

Oppretter personer i Mini-Norge slik at de finnes i PDL.

## Swagger
Swagger finnes under [/swagger](https://mn-person-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.
 
## Lokal kjøring
Ha naisdevice kjørende og kjør MNPersonServiceApplicationStarter med følgende argumenter:
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
