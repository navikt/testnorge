---
layout: default
title: Orkestratoren
parent: Applikasjoner
---

# Orkestratoren
Orkestratoren er applikasjonen som orkestrerer opprettelse av syntetiske hendelser i den syntetiske populasjonen "mininorge".

## Swagger
Swagger finnes under [/api](https://orkestratoren.nais.preprod.local/api) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisedevice kjørende og kjør LocalApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[copy token fra vault]
-Dspring.profiles.active=local
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må i tillegg ha følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
