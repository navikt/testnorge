---
layout: default
title: TPS ident service
parent: Applikasjoner
---

# testnorge-tps-ident-service

## Swagger
Swagger finnes under [/swagger](https://testnorge-tps-ident-service.dev.adeo.no/swagger) -endepunktet til applikasjonen.
 
## Lokal kjøring
Ha naisdevice kjørende og kjør TpsIdentServiceApplicationStarter med følgende argumenter:
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
