---
layout: default
title: testnav-skattekort-service
parent: Applikasjoner
---

# skattekort-service

Tjeneste for å legge til skattekort i os-eskatt ved innsending på kø

## Bruk

Applikasjonen eksponerer SOAP tjenesten 'person status' fra UDI, men med egne data.

Data opprettes via REST-tjenesten

For dokumentasjon av applikasjonen sine endepunkter:


## Lokal kjøring

Ha naisdevice kjørende og kjør UdiStubApplicationStarter med følgende argumenter:

```
--add-opens java.base/java.lang=ALL-UNNAMED
-Dspring.profiles.active=dev
-Dspring.cloud.vault.token=[vault-token]
```

