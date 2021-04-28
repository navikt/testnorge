---
layout: default
title: Testnorge MEDL
parent: Applikasjoner
---

# Testnorge-Medl
Testnorge-Medl er en applikasjon som henter syntetiske medlemskap (medlem i folketrygden) og populerer disse med identer før den legger medlemskapene inn i medl-databasen.

## Swagger
Swagger finnes under [/api](https://testnorge-medl.nais.preprod.local/api) -endepunktet til applikasjonen.

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
