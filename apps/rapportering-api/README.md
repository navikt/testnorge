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
  
### Utviklerimage
Kjør PersonApiApplicationStarter med følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
-Dspring.cloud.vault.token=[Copy token fra Vault]
-Dspring.profiles.active=dev
```

### Utenfor utviklerimage
   
#### Windows
Ha BIG-IP Edge Client kjørende og kjør RapporteringApiApplicationStarter med samme argumenter som for utviklerimage.
   
#### Mac
Ha Nav-Tunnel kjørende og kjør RapporteringApiApplicationStarter med samme argumenter som for utviklerimage og legg til følgende argumenter:
```
-DsocksProxyHost=127.0.0.1
-DsocksProxyPort=14122
-DsocksNonProxyHosts=slack.com
-Dhttp.nonProxyHosts=slack.com
```
