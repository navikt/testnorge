---
layout: default
title: Service
parent: Organisasjon
grand_parent: Applikasjoner
---

## Organisasjon-api
API for organsiasjoner.

### Swagger
Swagger finnes under [/swagger](https://testnorge-organisasjon-api.nais.preprod.local/swagger) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør OrganisasjonApiApplicationStarter med følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
-Dspring.profiles.active=dev
```
