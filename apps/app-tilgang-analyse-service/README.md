---
layout: default
title: app-tilgang-analyse-service
parent: Applikasjoner
---

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navit_testnorge_app_tilgang_analyse_service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=navit_testnorge_app_tilgang_analyse_service)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navit_testnorge_app_tilgang_analyse_service&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=navit_testnorge_app_tilgang_analyse_service)

# app-tilgang-analyse-service

Appen brukes for å finne avehingheter mellom apper basert på `inbound` og `outbound` regler i nias. 
 
## Swagger
Swagger finnes under [/api](https://testnav-app-tilgang-analyse-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring

### In Memmory DB
Ha naisdevice kjørende og kjør AppTilgangAnalyseServiceApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=local
```

### GCP DB

Kjør opp `cloud_sql_proxy`
```
./cloud_sql_proxy -instances=dolly-dev-ff83:europe-north1:testnav-app-tilgang-analyse-service=tcp:3306
```

Ha naisdevice kjørende og kjør AppTilgangAnalyseServiceApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=localdb
-DDB_PASSWORD=[passord for testnav-app-tilgang-analyse-service-db]
```