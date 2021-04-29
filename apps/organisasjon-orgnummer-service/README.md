---
layout: default
title: Organisasjonsnummer Service
parent: Organisasjon
grand_parent: Applikasjoner
---

# Organisasjonsnummer Service
App for å hente gyldige organisasjonsnummer som ikke er i bruk i EREG.

## Swagger
Swagger finnes under [/swagger](https://organisasjon-orgnummer-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.


## Lokal kjøring
Ha naisdevice kjørende og kjør OrgnummerServiceApplicationStarter med følgende argumenter:
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
