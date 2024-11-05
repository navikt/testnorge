# Bruk av GCP-database under lokal kjøring

Enkelte applikasjoner bruker en database i GCP som "lokal" database, dvs. i Spring profile _local_. P.t. gjelder dette:
* `dolly-backend`
* `organisasjon-forvalter`
* `pdl-forvalter`

Disse er refert til under som `APP_NAME`.

Applikasjonene har en noe annen konfigurasjon for kjøring lokalt, og bruker [gcloud CLI](https://doc.nais.io/operate/cli/reference/postgres/) og [cloud_sql_proxy](https://cloud.google.com/sql/docs/postgres/connect-auth-proxy).

* `cloud_sql_proxy` installeres med
```
> gcloud components install cloud-sql-proxy
```
* Du må være logget på med gcloud CLI.
```
> gcloud auth login --update-adc
```
* Du må starte `cloud_sql_proxy` med rett `APP_NAME` (se over).
```
> cloud_sql_proxy -instances=dolly-dev-ff83:europe-north1:testnav-APP_NAME-local=tcp:5432
```

Etter at proxy'en er startet kan du da kjøre den aktuelle applikasjonen lokalt. Applikasjonen henter selv passord vha. [Spring Cloud GCP](https://spring.io/projects/spring-cloud-gcp) ved oppstart.

Hvis du ønsker tilgang direkte til databasen gjennom en annen klient som IntelliJ så må du hente ut passordet vha.
```
> gcloud secrets versions access latest --secret=testnav-APP_NAME-local
```
JDBC connect URL vil være `jdbc:postgresql://localhost:5432/testnav-APP_NAME-local?user=testnav-APP_NAME-local`.