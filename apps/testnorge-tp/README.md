---
layout: default
title: Testnorge TP
parent: Applikasjoner
---

# Testnorge-TP
Testnorge-TP (Tjeneste Pensjon) er integrasjonen mellom Orkestratoren og TJPEN databasen. Testnorge-TP går mot TJPEN i gitte miljøer.
 
## Swagger
Swagger finnes under [/api](https://testnorge-tp.dev.intern.nav.no/api) -endepunktet til applikasjonen.
 
## Lokal kjøring
Ha naisdevice kjørende og kjør TestnorgeTpApplicationStarter med følgende argumenter:
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