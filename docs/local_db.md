# Hvordan sette opp en lokal PSQL for egen testing

Dette er en kort beskrivelse på hvordan du setter opp PSQL i Docker og fyller den med innhold hentet fra enten GCP eller FSS.

### Disclaimer:
> pg_dump does not block other users accessing the database (readers or writers).

## Eksportere fra GCP

Her bruker vi `dolly-backend` som eksempel. Vi bruker også [NAIS CLI](https://doc.nais.io/operate/cli/) som igjen avhenger av [gcloud CLI](https://cloud.google.com/sdk/gcloud). Eksport gjøres med [pg_dump](https://www.postgresql.org/docs/current/app-pgdump.html).

Først logg inn med gcloud CLI. NAIS CLI avhenger av dette.
~~~
> gcloud auth login --update-adc
~~~
Sett opp en proxy mot databasen [definert i applikasjonen](../apps/dolly-backend/config.yml) `dolly-backend`. Legg merke til hva databasen heter og la proxyen stå og kjøre mens du eksporterer i neste steg.
~~~
> nais postgres proxy dolly-backend
~~~
Setter opp en proxy for deg mot databasen til applikasjonen (her: `dolly-backend`).
~~~
> pg_dump --username=YOUR_NAV_EMAIL_ADDRESS --clean --create --no-owner --no-privileges --verbose --file=~/dump.sql testnav-dolly-backend
~~~
Output havner her i fila `~/dump.sql` og skal brukes ved import.

Legg merke til `--clean --create --no-owner --no-privileges`. Vi eksporterer til et script som tømmer eksisterende database, oppretter en ny database ved behov, og fjerner owner og groups på alle tabeller. Vi ønsker at eier av alle tabellene lokalt er default-brukeren `postgres`.

## Eksportere fra FSS

Her bruker vi `dolly-backend-dev` som eksempel. Eksport gjøres med [pg_dump](https://www.postgresql.org/docs/current/app-pgdump.html). Databasen er [definert i applikasjonen](../apps/dolly-backend/config.test.yml).
~~~
> pg_dump --host=dev-pg.intern.nav.no --username=USERNAME_FROM_VAULT --clean --create --no-owner --no-privileges --verbose --exclude-table=idents_from_* --exclude-table=diff_idents --exclude-table=test --file=~/dump.sql dolly-test
~~~
Brukernavn og passord hentes fra Vault, i dette tilfellet fra https://vault.adeo.no/ui/vault/secrets/postgresql%2Fpreprod-fss/credentials/dolly-test-admin.

Legg merke til at vi bruker `--exclude-table` i dette eksempelet. I denne databasen hadde vi noen tabeller med annen eier, som ikke lar seg eksportere med credentials fra Vault. I de fleste andre tilfeller kan du utelate `--exclude-table`.

## Sette opp PSQL i Docker

**Dette gjøres bare én gang.** Vi lager oss en container kalt `postgres`, der auth er slått av fra din Docker host. Default-brukeren `postgres` vil stå som eier av alle tabeller.
~~~
> docker run --name postgres -e POSTGRES_HOST_AUTH_METHOD=trust -p 5432:5432 postgres
~~~
Her kjøres containeren uten `--detach`, slik at vi kan følge med på logger i tilfelle feil under import, men det er valgfritt.

## Importere inn i PSQL

Import gjøres med [psql](https://www.postgresql.org/docs/current/app-psql.html). Et alternativ er [pg_restore](https://www.postgresql.org/docs/current/app-pgrestore.html), men da må eksporten gjøres med `--format=custom` og du kan ikke justere på SQLen før import ved behov.

En evt. eksisterende database vil bli erstattet, men du kan fint ha flere databaser for flere test-scenarier samtidig. Hvis du importerer fra de to eksemplene over så vil du for eksempel ha to databaser lokalt ved navn `testnav-dolly-backend` og `dolly-test`.
~~~
> psql --username=postgres --file=~\dump.sql
~~~