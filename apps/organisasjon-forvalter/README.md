---
layout: default
title: Forvalter
parent: Organisasjon
grand_parent: Applikasjoner
---

# Organisasjon Forvalter


## Lokal kjøring
Ha naisdevice kjørende og kjør OrganisasjonForvalterApplicationStarter med følgende argumenter:
``` 
-Dspring.profiles.active=dev
-Dspring.cloud.vault.token=[vault-token]
```

og legg til i VM options:
``` 
--add-opens java.base/java.lang=ALL-UNNAMED
``` 
