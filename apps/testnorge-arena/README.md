---
layout: default
title: Testnorge Arena
parent: Applikasjoner
---

# Testnorge-Arena

Testnorge-Arena applikasjonen som henter syntetiske vedtak, og velger ut identer som skal inn i disse vedtakene, før de sendes til Arena.

Applikasjonen har også støtte for å opprette syntetiske historiske vedtak, gjennom vedtakshistorikkendepunktet.

## Swagger
Swagger finnes under [/api](https://testnorge-arena.nais.preprod.local/api) -endepunktet til applikasjonen.

## Lokal kjøring
    
### Utenfor utviklerimage
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
