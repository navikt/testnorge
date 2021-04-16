---
layout: default
title: Testnorge SKD
parent: Applikasjoner
---


# Testnorge-Skd
Testnorge-skd er en applikasjon som henter syntetiske skd-meldinger og fyller disse med passende identer. Disse meldingene legges inn i en avspillergruppe i TPSF, før de "spilles av" og sendes til TPS.

Skd-meldingene har ulike typer, og forårsaker forskjellige endringer på personer i TPS. Det er slik mininorge berikes med nye identer og endringer på eksisterende identer.

## Swagger
Swagger finnes under [/api](https://testnorge-skd.nais.preprod.local/api) -endepunktet til applikasjonen.
 
## Lokal kjøring
      
### Utviklerimage
Kjør LocalApplicationStarter med følgende argumenter:

```
 -Djavax.net.ssl.trustStore=[path til lokal truststore]
 -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 -Dspring.cloud.vault.token=[Kopier token fra vault]
```

### Utenfor utviklerimage

#### Windows
Ha BIG-IP Edge Client kjørende og kjør LocalApplicationStarter med samme argumenter som for utviklerimage.
       
#### Mac
Ha Nav-Tunnel kjørende og kjør LocalApplicationStarter med samme argumenter som for utviklerimage og legg til følgende argumenter:

```
 -DsocksProxyHost=127.0.0.1
 -DsocksProxyPort=14122
 -DsocksNonProxyHosts=127.0.0.1|dl.bintray.com|repo.maven.apache.org|maven.adeo.no|packages.confluent.io|confluent.io|maven.xwiki.org|maven.repository.redhat.com
```

#### Med naisdevice
Trenger bare legge inn 
```
-Dspring.cloud.vault.token=[Kopi av token fra vault]
```
i VM options. Kjør med profil `local`.
