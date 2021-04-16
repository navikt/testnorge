---
layout: default
title: Testnorge Inst
parent: Applikasjoner
---

# Testnorge-Inst
Testnorge-Inst er en applikasjon som henter syntetiske institusjonsforholdsmeldinger og populerer disse med identer før den sender meldingene til Inst.

## Swagger
Swagger finnes under [/api](https://testnorge-inst.nais.preprod.local/api) -endepunktet til applikasjonen.

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
