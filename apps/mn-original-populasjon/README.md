---
layout: default
title: Original Populasjon
parent: Mini Norge
grand_parent: Applikasjoner
---


# Mini-Norge Original-populasjon
Mini-Norge-app som oppretter persondokumenter for et gitt antall personer. Aldersfordelt

## Swagger
Swagger finnes under [/swagger](https://mn-original-populasjon.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.
 
## Lokal kjøring
Ha naisdevice kjørende og kjør OriginalPopulasjonApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[Copy token fra Vault]
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må i tillegg ha følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```