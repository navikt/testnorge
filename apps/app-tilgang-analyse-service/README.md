[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navit_testnorge_app_tilgang_analyse_service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=navit_testnorge_app_tilgang_analyse_service)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navit_testnorge_app_tilgang_analyse_service&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=navit_testnorge_app_tilgang_analyse_service)

# app-tilgang-analyse-service

Appen brukes for å finne avhengigheter mellom apper basert på `inbound` og `outbound` regler i nais. 
 
## Swagger
Swagger finnes under [/swagger](https://testnav-app-tilgang-analyse-service.intern.dev.nav.no/swagger) -endepunktet til
applikasjonen.

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)

En egen Spring profile _localdb_ er satt opp for å kjøre med proxy mot applikasjonens reelle DB.

Kjør `cloud_sql_proxy` i bakgrunnen.
```
> cloud_sql_proxy -instances=dolly-dev-ff83:europe-north1:testnav-app-tilgang-analyse-service=tcp:3306
```
Start applikasjonen med Spring profile _localdb_ og definer DB_PASSORD (kan hentes fra pod).
```
-DDB_PASSWORD=[passord for testnav-app-tilgang-analyse-service-db]
```
