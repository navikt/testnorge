---
layout: default
title: Testnorge Sigrun
parent: Applikasjoner
---

## Testnorge-Sigrun
Testnorge-Sigrun er adapteren for opprettelse av pensjonsopptjeningsmeldinger som legges inn i sigrun-skd-stub.

### Swagger
Swagger finnes under [/api](https://testnorge-sigrun.dev.intern.nav.no/api) -endepunktet til applikasjonen.

### Lokal kjøring
Ha naisdevice kjørende og kjør TestnorgeSigrunApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
