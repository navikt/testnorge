## testnorge-statistikk-api
API for statistikk.

### Swagger
Swagger finnes under [/api](https://testnorge-statistikk-api.nais.preprod.local) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør StatistikkApiApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.profiles.active=dev
 
### Utenfor utviklerimage

#### Windows
Ha BIG-IP Edge Client kjørende og kjør StatistikkApiApplicationStarter med samme argumenter som for utviklerimage.
    
#### Mac
Ha Nav-Tunnel kjørende og kjør StatistikkApiApplicationStarter med samme argumenter som for utviklerimage og legg til følgende argumenter:
- -DsocksProxyHost=127.0.0.1
- -DsocksProxyPort=14122
- -DsocksNonProxyHosts=127.0.0.1|dl.bintray.com|repo.maven.apache.org|maven.adeo.no|packages.confluent.io|confluent.io|maven.xwiki.org|maven.repository.redhat.com
    