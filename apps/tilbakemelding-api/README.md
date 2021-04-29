---
layout: default
title: Tilbakemelding API
parent: Applikasjoner
---

# testnorge-tilbakemelding-api
App for innsending av tilbakemeldinger. Tilbakemeldingene blir publisert i slack

## Swagger
Swagger finnes under [/swagger](https://testnorge-tilbakemelding-api.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.


## Lokal kjøring
Ha naisdevice kjørende og kjør TilbakemeldingApiApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
