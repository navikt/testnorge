---
layout: default
title: Testnorge Inst
parent: Applikasjoner
---

# Testnorge-Inst
Testnorge-Inst er en applikasjon som henter syntetiske institusjonsforholdsmeldinger og populerer disse med identer før den sender meldingene til Inst.

## Swagger
Swagger finnes under [/api](https://testnorge-inst.dev.intern.nav.no/api) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør LocalApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
