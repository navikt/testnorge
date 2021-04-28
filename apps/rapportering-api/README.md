---
layout: default
title: Rapportering API
parent: Applikasjoner
---

# Testnorge-Rapportering-api
API for rapportering av tilbakemeldinger til slack.

## Swagger
Swagger finnes under [/api](https://testnorge-rapportering-api.nais.preprod.local/api) -endepunktet til applikasjonen.
 
## Lokal kjøring
Ha naisdevice kjørende og kjør RapporteringApiApplicationStarter med følgende argumenter:
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
