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
Ha naisdevice kjørende og kjør SyntSykemeldingApiApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må i tillegg ha følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
    
