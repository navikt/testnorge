---
layout: default
title: Original Populasjon
parent: Mini Norge
grand_parent: Applikasjoner
---


# mn-original-populasjon
Mini-Norge-app som oppretter persondokumenter for et gitt antall personer. Aldersfordelt

## Swagger
Swagger finnes under [/swagger](https://mn-original-populasjon.dev.adeo.no/swagger) -endepunktet til applikasjonen.
 
## Lokal kjøring
  
### Utviklerimage
Kjør PersonApiApplicationStarter med følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
-Dspring.cloud.vault.token=[Copy token fra Vault]
-Dspring.profiles.active=dev
```

### Utenfor utviklerimage
   
#### Windows
Ha BIG-IP Edge Client eller Naisdevice kjørende og kjør OriginalPopulasjonApplicationStarter med samme argumenter som for utviklerimage.
   
#### Mac
Ha Nav-Tunnel kjørende og kjør OriginalPopulasjonApplicationStarter med samme argumenter som for utviklerimage og legg til følgende argumenter:
```
-DsocksProxyHost=127.0.0.1
-DsocksProxyPort=14122
-DsocksNonProxyHosts=127.0.0.1|dl.bintray.com|repo.maven.apache.org|maven.adeo.no|packages.confluent.io|confluent.io|maven.xwiki.org|maven.repository.redhat.com
```   
