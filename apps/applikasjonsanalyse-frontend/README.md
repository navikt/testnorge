---
layout: default
title: Frontend
parent: Avhengighetsanalyse
grand_parent: Applikasjoner
---

# Avhengighetsanalyse-Frontend
Frontend for avhengighets analyse.

### Swagger
Swagger finnes under [/api](https://testnav-applikasjonsanalyse-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.


### Lokal kjøring
Kjør AvhengighetsanalysteFrontendApplicationStarter med følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
-Dspring.profiles.active=dev
```
