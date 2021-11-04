---
layout: default
title: TPS Messaging Service
parent: Applikasjoner
---

# TPS-messaging-service
TPS-messaging-service gir mulighet å sende og motta XML-meldinger mot TPS MQ-køer, samt lese XML-innhold fra TPS servicerutiner over CICS.
 
## Swagger
Swagger finnes under [/api](https://testnav-tps-messaging-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør TpsMessagingServiceApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=local
--add-opens java.base/java.lang=ALL-UNNAMED
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
