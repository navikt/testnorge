---
layout: default
title: testnav-skattekort-service
parent: Applikasjoner
---

# skattekort-service

Tjeneste for å legge til skattekort i os-eskatt ved innsending til deres eget API for test.

## Bruk

Applikasjonen omformer JSON-request til XML-request og sender inn på overnevnte endepunkt.


## Lokal kjøring

Ha naisdevice kjørende og kjør SkattekortServiceApplicationStarter med følgende argumenter:

```
--add-opens java.base/java.lang=ALL-UNNAMED
-Dspring.profiles.active=dev
-Dspring.cloud.vault.token=[vault-token]
```

