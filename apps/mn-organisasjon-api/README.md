---
layout: default
title: Organisasjon API
parent: Mini Norge
grand_parent: Applikasjoner
---

# Mini-Norge Organisasjon API
Applikasjon for å opprette Mini-Norge organisasjoner.

## Swagger
Swagger finnes under [/swagger](https://mn-organisasjon-api.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør MNOrganisasjonApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[vault-token]
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
