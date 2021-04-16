---
layout: default
title: Synt Person API
parent: Applikasjoner
---

## Testnorge-Synt-person-api
API for syntetisering av personer

### Swagger
Swagger finnes under [/swagger](https://testnorge-synt-person-api.dev.adeo.no/swagger) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør SyntSykemeldingApiApplicationStarter med følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
-Dspring.profiles.active=dev
```
 
 ### Utenfor utviklerimage
 
 #### Windows
SyntPersonApplicationStarter med samme argumenter som for utviklerimage.
   
 #### Mac
```
-DsocksProxyHost=127.0.0.1
-DsocksProxyPort=14122
-DsocksNonProxyHosts=127.0.0.1|dl.bintray.com|repo.maven.apache.org|maven.adeo.no|packages.confluent.io|confluent.io|maven.xwiki.org|maven.repository.redhat.com
```
