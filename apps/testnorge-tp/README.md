---
layout: default
title: Testnorge TP
parent: Applikasjoner
---

# Testnorge-TP
Testnorge-TP (Tjeneste Pensjon) er integrasjonen mellom Orkestratoren og TJPEN databasen. Testnorge-TP går mot TJPEN i gitte miljøer.
 
## Swagger
Swagger finnes under [/api](https://testnorge-tp.nais.preprod.local/swagger-ui.html) -endepunktet til applikasjonen.
 
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
