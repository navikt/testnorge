---
layout: default
title: Testnorge SAM
parent: Applikasjoner
---

# Testnorge-Sam
Testnorge-Sam er adapteren mellom orkestratoren og Sam for opprettelse av syntetiske samordningsmeldinger.

## Swagger
Swagger finnes under [/api](https://testnorge-sam.nais.preprod.local/api) -endepunktet til applikasjonen.
  
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
