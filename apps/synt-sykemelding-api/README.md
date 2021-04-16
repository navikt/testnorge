---
layout: default
title: Synt Sykemelding API
parent: Applikasjoner
---

## Synt-Sykemelding-api
API for syntetisering av sykemeldinger

### Swagger
Swagger finnes under [/swagger](https://testnorge-synt-sykemelding-api.dev.adeo.no/swagger) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør SyntSykemeldingApiApplicationStarter med følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
-Dspring.profiles.active=dev
```
