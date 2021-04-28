---
layout: default
title: Testnorge NAV Endringsmeldinger
parent: Applikasjoner
---

# Testnorge-Nav-Endringsmeldinger
Testnorge-Nav-Endringsmeldinger er adapteren for opprettelse av nav-endringsmeldinger som sendes til TPS som XML-meldinger gjennom TPSF.

## Swagger
Swagger finnes under [/api](https://testnorge-nav-endringsmeldinger.nais.preprod.local/api) -endepunktet til applikasjonen.
 
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
