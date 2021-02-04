# Testnorge-Arena

Testnorge-Arena applikasjonen som henter syntetiske vedtak, og velger ut identer som skal inn i disse vedtakene, før de sendes til Arena.

Applikasjonen har også støtte for å opprette syntetiske historiske vedtak, gjennom vedtakshistorikkendepunktet.

## Swagger
Swagger finnes under [/api](https://testnorge-arena.nais.preprod.local/api) -endepunktet til applikasjonen.

## Lokal kjøring
   
### Utviklerimage
Kjør ApplicationStarter med følgende argumenter:
 ```
 -Djavax.net.ssl.trustStore=[path til lokal truststore]
 -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 -Dspring.cloud.vault.token=[Kopi av token fra vault]
 -Dspring.profiles.active=local
 ```
    
### Utenfor utviklerimage

#### Windows
Ha BIG-IP Edge Client kjørende og kjør LocalApplicationStarter med samme argumenter som for utviklerimage.
    
#### Mac
Ha Nav-Tunnel kjørende og kjør LocalApplicationStarter med samme argumenter som for utviklerimage og legg til følgende argumenter:
- -DsocksProxyHost=127.0.0.1
- -DsocksProxyPort=14122
- -DsocksNonProxyHosts=127.0.0.1|dl.bintray.com|repo.maven.apache.org|maven.adeo.no|packages.confluent.io|confluent.io|maven.xwiki.org|maven.repository.redhat.com

#### Med naisdevice
Trenger bare legge inn 
```
-Dspring.cloud.vault.token=[Kopi av token fra vault]
-Dspring.profiles.active=local
```
i VM options.