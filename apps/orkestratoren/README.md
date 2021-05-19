---
layout: default
title: Orkestratoren
parent: Applikasjoner
nav_order: 1
---

# Orkestratoren
Orkestratoren er applikasjonen som orkestrerer opprettelse av syntetiske hendelser i den syntetiske nav-populasjonen "mininorge".

## Swagger
Swagger finnes under [/swagger](https://orkestratoren.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisedevice kjørende og kjør LocalApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[copy token fra vault]
-Dspring.profiles.active=local
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
