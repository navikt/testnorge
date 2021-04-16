---
layout: default
title: Person API
parent: Applikasjoner
---

# Testnorge-Person-api
API for person.

## Swagger
Swagger finnes under [/swagger](https://testnorge-person-api.nais.preprod.local/swagger) -endepunktet til applikasjonen.
 
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
Ha BIG-IP Edge Client kjørende og kjør PersonApiApplicationStarter med samme argumenter som for utviklerimage.
   
#### Mac
Ha Nav-Tunnel kjørende og kjør PersonApiApplicationStarter med samme argumenter som for utviklerimage og legg til følgende argumenter:
```
-DsocksProxyHost=127.0.0.1
-DsocksProxyPort=14122
-DsocksNonProxyHosts=127.0.0.1|dl.bintray.com|repo.maven.apache.org|maven.adeo.no|packages.confluent.io|confluent.io|maven.xwiki.org|maven.repository.redhat.com
```
