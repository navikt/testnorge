---
layout: default
title: Testnorge Populasjoner
parent: Applikasjoner
---

# Testnorge-populasjoner

En applikasjon som lytter på hendelser fra PDl og trekker ut identer som tilhører TENOR, som lagres i database og som enkelt kan hentes fra andre applikasjoner gjennom REST-api.

## Swagger
Swagger finnes under [/api](https://testnorge-populasjoner.dev.intern.nav.no/api) -endepunktet til applikasjonen.
 
## Lokal kjøring
Ha naisdevice kjørende og kjør ApplicationStarter med følgende argumenter:
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