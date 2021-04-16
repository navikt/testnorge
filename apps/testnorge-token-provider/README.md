---
layout: default
title: Testnorge Token Provider
parent: Applikasjoner
---

# Testnorge-Token-Provider
Token provider for testnorge. 

## Swagger
Swagger finnes under [/swagger-ui.html](https://testnorge-token-provider.nais.preprod.local/swagger-ui.html) -endepunktet til applikasjonen.

## Lokal kjøring
      
### Utviklerimage
Kjør ApplicationStarter med følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
-Dspring.cloud.vault.token=[Kopier token fra vault]
-Dspring.profiles.active=local
```
   
### Utenfor utviklerimage
   
#### Windows
Ha BIG-IP Edge Client kjørende og kjør ApplicationStarter med samme argumenter som for utviklerimage.
       
#### Mac
Ha Nav-Tunnel kjørende og kjør ApplicationStarter med samme argumenter som for utviklerimage og legg til følgende argumenter:
```
-DsocksProxyHost=127.0.0.1
-DsocksProxyPort=14122
-DsocksNonProxyHosts=127.0.0.1|dl.bintray.com|repo.maven.apache.org|maven.adeo.no|packages.confluent.io|confluent.io|maven.xwiki.org|maven.repository.redhat.com
```
