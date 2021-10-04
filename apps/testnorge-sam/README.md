---
layout: default
title: Testnorge SAM
parent: Applikasjoner
---

# Testnorge-Sam
Testnorge-Sam er adapteren mellom orkestratoren og Sam for opprettelse av syntetiske samordningsmeldinger.

## Swagger
Swagger finnes under [/api](https://testnorge-sam.dev.intern.nav.no/api) -endepunktet til applikasjonen.
  
## Lokal kjøring
Ha naisdevice kjørende og kjør TestnorgeSamApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=local
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
-Dspring.profiles.active=local
```
