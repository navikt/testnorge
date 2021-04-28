---
layout: default
title: Testnorge TP
parent: Applikasjoner
---

# Testnorge-TP
Testnorge-TP (Tjeneste Pensjon) er integrasjonen mellom Orkestratoren og TJPEN databasen. Testnorge-TP går mot TJPEN i gitte miljøer.
 
## Swagger
Swagger finnes under [/api](https://testnorge-tp.nais.preprod.local/swagger-ui.html) -endepunktet til applikasjonen.
 
## Lokal kjøring
Ha naisdevice kjørende og kjør LocalApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må i tillegg ha følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```