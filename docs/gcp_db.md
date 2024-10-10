# Bruk av GCP-database under lokal kjøring

Enkelte applikasjoner bruker en database i GCP som "lokal" database, dvs. i Spring profile _local_. P.t. gjelder dette:
* `dolly-backend`
* `organisasjon-forvalter`
* `pdl-forvalter`

Disse er refert til under som `APP_NAME`.

Applikasjonene har en noe annen konfigurasjon for kjøring lokalt, og bruker [gcloud CLI](https://doc.nais.io/operate/cli/reference/postgres/) og [cloud_sql_proxy](https://cloud.google.com/sql/docs/mysql/sql-proxy).

* Du må være logget på med gcloud CLI.
```
> gcloud auth login --update-adc
```
* Du må starte `cloud_sql_proxy` med rett `APP_NAME` (se over). **Legg merke til bruken av `local-` her.**
```
> cloud_sql_proxy -instances=dolly-dev-ff83:europe-north1:local-APP_NAME=tcp:5432
```

Etter at proxy'en er startet kan du da kjøre den aktuelle applikasjonen lokalt. Applikasjonen henter selv passord vha. [Spring Cloud GCP](https://spring.io/projects/spring-cloud-gcp) ved oppstart.

Hvis du ønsker tilgang direkte til databasen gjennom en annen klient som IntelliJ så må du hente ut passordet vha.
```
> gcloud secrets versions access latest --secret=db-APP_NAME
```
Brukernavnet er `db-APP_NAME`. JDBC connect URL vil være `jdbc:postgresql://localhost:5432/db-APP_NAME`.