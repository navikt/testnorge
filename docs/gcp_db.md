# Opprette GCP-database for lokal kjøring
1. Opprett en database i [Google Cloud Console](https://console.cloud.google.com/sql/instances/). Eksempelvis `testnav-min-database`.
2. Opprett en bruker i denne databasen (klikk på databasen, velg _Users_, velg _Add user account_.). Eksempelvis `testnav-min-database`. **Ta vare på generert passord til neste steg!**
3. Opprett en secret for den nye databasebrukeren i [Google Cloud Console](https://console.cloud.google.com/security/secret-manager). Eksempelvis `testnav-min-database`. Legg inn det genererte passordet her, slik at applikasjoner kan konfigureres opp med passordet som en secret `${sm\://testnav-min-database}`.
4. Databaseinstansen er opprettet, men det er ingen database (skjema) der ennå. Bruk en SQL-klient (f.eks. IntelliJ) og logg på databasen med brukeren opprettet i trinn 3, men mot databasen `postgres`. Opprett databasen med `create database "testnav-min-database";`
5. Nå kan du logge på databaseinstansen `testnav-min-database` mot databasen `testnav-min-database` med brukeren `testnav-min-database`.

Hvis du ønsker å rotere passord er det bare å generere et nytt for databasebrukeren og oppdatere secret tilsvarende.

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
* Du må starte `cloud-sql-proxy` med rett `APP_NAME` (se over).
```
> cloud-sql-proxy dolly-dev-ff83:europe-north1:testnav-APP_NAME-local -p 5432
```

Etter at proxy'en er startet kan du da kjøre den aktuelle applikasjonen lokalt. Applikasjonen henter selv passord vha. [Spring Cloud GCP](https://spring.io/projects/spring-cloud-gcp) ved oppstart.

Hvis du ønsker tilgang direkte til databasen gjennom en annen klient som IntelliJ så må du hente ut passordet vha.
```
> gcloud secrets versions access latest --secret=testnav-APP_NAME-local
```
JDBC connect URL vil være `jdbc:postgresql://localhost:5432/testnav-APP_NAME-local?user=testnav-APP_NAME-local`.