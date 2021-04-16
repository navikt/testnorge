---
layout: default
title: Testnorge Populasjoner
parent: Applikasjoner
---

# Testnorge-populasjoner

En applikasjon som lytter på hendelser fra PDl og trekker ut identer som tilhører TENOR, som lagres i database og som enkelt kan hentes fra andre applikasjoner gjennom REST-api.

## Swagger
Swagger finnes under [/api](https://testnorge-populasjoner.nais.preprod.local/api) -endepunktet til applikasjonen.
 
## Lokal kjøring
      
### Utviklerimage
Kjør ApplicationStarter med følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
-Dspring.profiles.active=local
-Dspring.cloud.vault.token=[Kopier token fra vault]
```
