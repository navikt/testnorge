---
layout: default
title: Team Token Frontend
parent: Applikasjoner
---

# team-token-frontend

Appliaksjon for bruk av Team Dolly til å kunne genere personlige tokens til bruk i apper som ikke støtter Client Credential (service brukere).
Det er kun team dolly som bruker denne appen. 

Lenke https://testnorge-oversikt-frontend.dev.adeo.no/ 

## Lokal kjøring
   
### Utviklerimage
Kjør SykemeldingApiApplicationStarter med følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=dev
```

### Utenfor utviklerimage
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=dev
```
