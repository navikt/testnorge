# mn-person-service

## Swagger
Swagger finnes under [/swagger](https://mn-person-service.dev.adeo.no/swagger) -endepunktet til applikasjonen.
 
## Lokal kjøring
  
### Utviklerimage
Kjør PersonApiApplicationStarter med følgende argumenter:
 - -Dspring.cloud.vault.token=[Copy token fra Vault]
 - -Dspring.profiles.active=dev
   
### Utenfor utviklerimage
   
#### Windows
Ha BIG-IP Edge Client eller Naisdevice kjørende og kjør OriginalPopulasjonApplicationStarter med samme argumenter som for utviklerimage.
   
#### Mac
Ha Nav-Tunnel kjørende og kjør OriginalPopulasjonApplicationStarter med samme argumenter som for utviklerimage og legg til følgende argumenter:
- -DsocksProxyHost=127.0.0.1
- -DsocksProxyPort=14122
- -DsocksNonProxyHosts=127.0.0.1|dl.bintray.com|repo.maven.apache.org|maven.adeo.no|packages.confluent.io|confluent.io|maven.xwiki.org|maven.repository.redhat.com
   