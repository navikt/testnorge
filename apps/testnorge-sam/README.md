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
      
### Utviklerimage
Kjør LocalApplicationStarter med følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
-Dspring.cloud.vault.token=[Kopier token fra vault]
```
