---
layout: default
title: Team Token Frontend
parent: Applikasjoner
---

# Team Token Frontend

Applikasjon for bruk av Team Dolly til å kunne generere personlige tokens til bruk i apper som ikke støtter Client Credential (service brukere).
Det er kun Team Dolly som bruker denne appen. 

Lenke https://testnav-oversikt.dev.adeo.no/ 

## Lokal kjøring
Ha naisdevice kjørende og kjør OversiktFrontendApplicationStarter med følgende argumenter:
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
