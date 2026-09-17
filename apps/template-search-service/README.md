# Template Search Service
Tjeneste som lagrer og henter søkemaler (maler) for Tenor-søk.

Tilgang og eierskap:
* Azure-brukere, også de som representerer et team, ser alle Azure- og teammaler.
* BankID-brukere ser kun maler som eies av brukere i samme organisasjon, slått opp via `testnav-bruker-service`.
* Maler lagres på personlig bruker eller representert team. Navneendring og sletting krever samme eierskap.

Fødselsnummer og d-nummer kan ikke lagres i maler, og valideres bort både i nøkler og verdier.
Malnavn tillater tegnsetting, inkludert komma og utropstegn. Navnet må inneholde tekst og kan være opptil 100 tegn.
Mellomrom før og etter navnet fjernes. Kontrolltegn inne i navnet, som linjeskift og tabulator, avvises.

## Miljøer og teameierskap

| Instans | Dolly-backend | Innlogging |
| --- | --- | --- |
| `testnav-template-search-service` | `dolly-backend` | Azure, team og BankID |
| `testnav-template-search-service-dev` | `dolly-backend-dev` | Azure og team |

Instansene har hver sin PostgreSQL-instans. Teamets bruker-ID lagres uten miljøprefiks, siden databasene er adskilt.
Dev får en ny, tom database. Non-dev gjenbruker den eksisterende PostgreSQL-instansen, men Flyway sletter de gamle dev-testmalene én gang før den tas i bruk som non-dev.
Malene flyttes ikke automatisk mellom brukere, team eller miljøer.

Ved lagring, navneendring og sletting for Azure-brukere slår tjenesten opp `/api/v1/bruker/current` med OBO-token mot sin konfigurerte Dolly-backend.
Den bruker teamets `brukerId` når et team er aktivt, ellers den autentiserte Azure-brukerens ID.
Oppslaget gjøres på nytt for hver skriveoperasjon. Feil i oppslaget gir feilrespons, ikke personlig eierskap som reservevalg.
Lesing bruker den autentiserte brukeren uten å slå opp aktivt team. `ALLE` viser alle Azure- og teammaler, mens en angitt `brukerId` filtrerer på den valgte eieren.

For team lagres den stabile teamidentiteten, ikke teamnavnet.
Når `/oversikt` inneholder teammaler, hentes teamnavn med ett `GET /api/v1/team` mot riktig Dolly-backend for hver forespørsel.
Teamets `brukerId` brukes som koblingsnøkkel, ikke den numeriske team-ID-en.
Navnet returneres i `brukernavn`, mens `brukerId` beholdes uendret. Teamnavn caches ikke og skrives ikke tilbake til databasen.
Manglende navn, feil i oppslaget eller navn med personidentifikator gir visningsnavnet «Ukjent team» og en loggadvarsel uten persondata. Malene vises fortsatt.
Selve malresponsen beholder dagens felt; visningsnavnene ligger i eieroversikten.

BankID bruker fortsatt signert `User-Jwt` og organisasjonsoppslag i bruker-service, uten å kontakte Dolly-backend.
Dev har ikke TokenX-konfigurasjon eller BankID-verifikasjonsnøkkel. Ingen instans leser teamtilstand fra `User-Jwt`.

Frontendansvarlig kobler dev og lokal kjøring til `https://testnav-template-search-service-dev.intern.dev.nav.no`
med Azure-scope `api://dev-gcp.dolly.testnav-template-search-service-dev/.default`.
Non-dev bruker `https://testnav-template-search-service.intern.dev.nav.no` og tilsvarende scope uten `-dev`.
API-stiene er uendret. Frontend må også ha nødvendige outbound-regler.

Begge instansene tillater `testnav-oversikt-frontend` som caller. Oversikt finner appene gjennom inbound-reglene i manifestene på GitHub.
Regelendringer må deployes før den nye tilgangen kan brukes. App-listen kan være cachet i ti minutter.

## Database og drift

Begge instansene bruker PostgreSQL 15 og `db-f1-micro`, med delt CPU og omtrent 614 MB RAM.
Denne størrelsen er ikke dekket av Cloud SQL-SLA. Dev får 10 GB HDD; eksisterende hoveddatabase beholder 10 GB SSD.
Det brukes ikke standby-instans eller automatisk diskvekst. Nais beholder nattlig backup med standard oppbevaring på sju kopier.
Tilkoblingspoolen er begrenset til tre forbindelser per app-instans.
Se [Nais om Cloud SQL](https://docs.nais.io/persistence/cloudsql/) og [diskvalg](https://docs.nais.io/persistence/cloudsql/explanations/cloud-sql-instance/).

Nedskalering av den eksisterende instansen fra `db-custom-1-3840` til `db-f1-micro` krever omstart og kan gi noen minutters utilgjengelighet.
Eksisterende instansnavn, databasenavn, disk og legitimasjonsbindinger beholdes. Ingen Nais-ressurser overføres mellom applikasjoner.

### Engangsrydding av testmaler

`V2__delete_legacy_dev_templates.sql` inneholder:

```sql
delete from tenor_person_mal;
```

Godkjenn slettingen før denne versjonen deployes. Flyway kjører migreringen automatisk ved første oppstart.
Den sletter alle eksisterende maler i template-service sin egen database, inkludert maler med tidligere dev-prefiks.
Den berører ikke bruker-service, Dolly-databasene eller andre tabeller. Tabellstrukturen og ID-sekvensen beholdes.
I den nye dev-databasen er tabellen allerede tom når migreringen kjører.

Flyway registrerer migreringen som utført. Nye maler beholdes ved senere oppstarter og deployments.
Ikke slett Flyway-historikken eller kjør slettingen manuelt i tillegg.
Slettede testmaler kan ikke gjenopprettes med en kode-rollback.

## Deployment og rollback

Workflowen bruker `#deploy-template-search-service` for non-dev og `#deploy-test-template-search-service` for dev.
Dev-workflowen kan deploye før den separate testjobben er ferdig; begge må være grønne før overgangen godkjennes.

Løsningen er foreløpig bare tatt i bruk i dev, og nedetid under overgangen er akseptabelt.
Backendendringene kan derfor deployes samtidig, uten midlertidige tilgangsregler eller en trinnvis overgang.

1. Godkjenn engangsryddingen. Stans mallagring og vent til pågående kall er ferdige før deployment, slik at gamle dev-kall ikke skriver nye data etter tømmingen.
2. Deploy bruker-service, begge template-instansene og tilgangsreglene i begge Dolly-backendene. Frontendansvarlig kobler dev og lokal kjøring til dev-instansen.
3. Hold mallagring stanset til gamle template-pods er avsluttet, alle deployments er ferdige, Flyway-migreringen er fullført og frontend-rutingen er oppdatert. Mellomversjoner kan gi feil maleier. Første opprettelse av dev-databasen kan ta flere minutter.
4. Kontroller at begge miljøer starter uten maler. Kontroller deretter at Azure-brukere ser både personlige maler og teammaler, at teambytte endrer maleier ved lagring, og at samme team-ID i de to miljøene ikke deler data. Kontroller også eierkontroll ved endring og sletting, oppdatert teamnavn etter navneendring i Dolly og BankID i non-dev.

Ved rollback må bruker-service, template-service og tilgangsreglene tilbakeføres til versjoner som hører sammen.
Koordiner også eventuell tilbakeføring av frontend-rutingen, og vent med mallagring til tilbakeføringen er ferdig.
Behold begge databasene; ikke slett eller flytt maler som del av rollback.
Databasekapasiteten kan settes tilbake ved behov, men det krever en ny omstart.

Følg readiness, HTTP-feil og responstid via eksisterende helseendepunkter og metrikker.
Følg også ledig diskplass, minne og forbindelser i Cloud SQL. Sett varsling før disken blir full, siden automatisk vekst er av.
Feilsøking skal ikke logge tokens, brukeridentifikatorer eller søkekriterier.

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)

Lokalt kjører tjenesten mot en H2-database i minnet.
