---
layout: default
title: Service
parent: Endringsmelding
grand_parent: Applikasjoner
---

# Endringsmelding-service

## Swagger
Swagger finnes under [/swagger](https://endringsmelding-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.
For å kunne bruke endringsmelding-endepunktet må det innsendte tokenet ha tilgang via azure applikasjonen `dev-gcp:dolly:endringsmelding-frontend`.
 
## Lokal kjøring
Kjør EndringsmeldingServiceApplicationStarter med følgende argumenter:
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