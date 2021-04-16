---
layout: default
title: Syntetisk Arbeidsforhold Service
parent: Mini Norge
grand_parent: Applikasjoner
---

# Mini-Norge Syntetisk Arbeidsforhold Service

Oppretter syntetiske arbeidsforhold i Mini-Norge

## Swagger
Swagger finnes under [/swagger](https://mn-synt-arbeidsforhold-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.
 
## Lokal kjøring
  
### Utviklerimage
Kjør MNSyntArbeidsforholdServiceApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[Copy token fra Vault]
-Dspring.profiles.active=dev
```

### Utenfor utviklerimage
   
#### Windows
Ha BIG-IP Edge Client eller Naisdevice kjørende og kjør MNSyntArbeidsforholdServiceApplicationStarter med samme argumenter som for utviklerimage.
   
#### Mac
Ha Nav-Tunnel kjørende og kjør MNSyntArbeidsforholdServiceApplicationStarter med samme argumenter som for utviklerimage og legg til følgende argumenter:
```
-DsocksProxyHost=127.0.0.1
-DsocksProxyPort=14122
-DsocksNonProxyHosts=127.0.0.1|dl.bintray.com|repo.maven.apache.org|maven.adeo.no|packages.confluent.io|confluent.io|maven.xwiki.org|maven.repository.redhat.com
```
