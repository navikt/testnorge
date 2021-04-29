---
layout: default
title: Testnorge SKD
parent: Applikasjoner
---


# Testnorge-Skd
Testnorge-skd er en applikasjon som henter syntetiske skd-meldinger og fyller disse med passende identer. Disse meldingene legges inn i en avspillergruppe i TPSF, før de "spilles av" og sendes til TPS.

Skd-meldingene har ulike typer, og forårsaker forskjellige endringer på personer i TPS. Det er slik mininorge berikes med nye identer og endringer på eksisterende identer.

## Swagger
Swagger finnes under [/api](https://testnorge-skd.dev.intern.nav.no/api) -endepunktet til applikasjonen.
 
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
