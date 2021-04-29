---
layout: default
title: Frontend
parent: Avhengighetsanalyse
grand_parent: Applikasjoner
---

# Avhengighetsanalyse-Frontend
Frontend for avhengighetsanalyse.

Endepunkt: https://testnav-applikasjonsanalyse.dev.intern.nav.no/

### Lokal kjøring
Ha naisdevice kjørende og kjør AvhengighetsanalysteFrontendApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[Copy token fra Vault]
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```