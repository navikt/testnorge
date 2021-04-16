---
layout: default
title: Hodejegeren
parent: Applikasjoner
---

# Testnorge-Hodejegeren
Testnorge-Hodejegeren er en applikasjon som henter identer fra avspillergrupper i TPSF. Den har mulighet til å filtrere identene på en rekke ulike egenskaper, og kan også returnere detaljert informasjon om identene, hentet fra TPS.

Applikasjonen tilbyr også en søkefunksjon, der man kan søke etter visse egenskaper, og få identene som tilfredsstiler egenskapene i retur. Dette er mulig fordi applikasjonen har sin egen database som lagrer informasjon om identene, noe som muligjør indeksering av egenskaper.

## Swagger
Swagger finnes under [/api](https://testnorge-hodejegeren.nais.preprod.local/api) -endepunktet til applikasjonen.
 
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
     
#### Naisdevice
Kjør LocalApplicationStarter med
``` 
-Dspring.cloud.vault.token=[Kopi av token fra vault]
```
i VM-options 

**Notat:**  
MongoDB nås ikke utenfor utviklerimage.
