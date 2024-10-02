# Bruk av GCP-database under lokal kjøring

Enkelte applikasjoner bruker en database i GCP som "lokal" database, dvs. i Spring profile _local_. P.t. gjelder dette:
* `dolly-backend`
* `organisasjon-forvalter`
* `pdl-forvalter`

På grunn av begrensninger i NAIS må disse databasene tilhøre en applikasjon. Det finnes en [../apps/dolly-db](../apps/dolly-db) som deployes flere ganger for å opprette og "eie" disse databasene.

Disse har derfor en noe annen konfigurasjon for kjøring lokalt, og bruker [NAIS CLI](https://doc.nais.io/operate/cli/reference/postgres/).

* Du må være logget på med gcloud CLI.
* ~~Databasene må forberedes for tilgang.~~ **En gang, dette er allerede gjort.**
```
> nais postgres prepare --all-privs <db-app-navn>
```
* Du må gi din personlige bruker tilgang til databasen. **En gang.**
```
> nais postgres grant <db-app-navn>
```
* Tilgang til DB gis gjennom NAIS CLI.
```
> nais postgres proxy <db-app-navn>
```
* Brukernavnet må være din NAV-ident. Du må enten:
   * Sette en miljøvariabel `NAV_USERNAME` til din NAV-ident. **En gang.**
   * Endre brukernavnet i den aktuelle application-local.yml fra `${NAV_USERNAME}` til ditt navn. **Hver gang. Og ikke commit'e den endringen.**

Etter at proxy'en er startet kan du da kjøre den aktuelle applikasjonen lokalt.