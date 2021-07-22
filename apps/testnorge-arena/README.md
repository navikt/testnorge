---
layout: default
title: Testnorge Arena
parent: Applikasjoner
---

# Testnorge-Arena
Testnorge-Arena applikasjonen henter syntetisk vedtakshistorikk, velger ut passende identer for historikken, registrerer
nødvendig annen informasjon på utvalgt ident og sender historikken til Arena.

## Swagger
Swagger finnes under [/api](https://testnorge-arena.dev.intern.nav.no/api) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør ApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[Kopi av token fra vault]
-Dspring.profiles.active=local
```

NB! Aktørregisteret kan ikke nåes utenfor utviklerimage så flere endepunkt i Testnorge-Arena (bl.a generering av vedtakshistorikk) vil ikke 
fungere utenfor utviklerimage.

### Utviklerimage
I utviklerimage må du i tillegg ha følgende argumenter:
 ```
 -Djavax.net.ssl.trustStore=[path til lokal truststore]
 -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 ```
