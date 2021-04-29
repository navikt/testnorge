---
layout: default
title: TPS Ident Vedlikehold
parent: Applikasjoner
---

# TPS-Identer-Vedlikehold
TPS-Identer-Vedlikehold gir mulighet for sletting av overflødige identer. 
Identer kan slettes mot TPSF som sletter videre mot TPS og Ident-Pool.
 
## Swagger
Swagger finnes under [/swagger](https://tps-identer-vedlikehold.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør TpsIdenterVedlikeholdApplicationStarter med følgende argumenter:
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