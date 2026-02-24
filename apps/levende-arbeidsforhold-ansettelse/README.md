# Levende arbeidsforhold-ansettelse

## Forbedringspotensial
* Nå så blir samme antall mennesker ansatt i hver organisasjon og vi tenker at dette er noe som kan bli forbedret.
* Vi har ikke lagt til forskjellige arbeidsforhold typer noe som kan gjøre dette mer realistisk.
* Vi gjør nå to oppslag i search-tenor-service noe som vi tenker kan reduseres til en ved å gjøre endringer i tenor-searc-service eller forandre hvordan denne appen finner organisasjoner.
* Å gjøre mengden mennesker som blir hentet i PdlService mer dynamisk.
* Skrive om til å bruke asynchronous (flux) overalt.

## Flyt
For å starte en jobb så bruker man endepunktet "/api/ansettelse-jobb", som vil kalle på runAnsettelsesService i AnsettelseService.java.

Det er i AnsettelsesService hvor selve logikken i applikasjonen blir gjennomført. 
Flyten i AnsettelsesService:
1. i AnsettelsesService er at runAnsettelsesService lager en thread slik at vi kunne implementere en timeout funksjon i tilfelle
det skjedde en uendelig loop.
2. ansettelseService blir kalt.
3. Henter yrke kodene fra KodeverkService -> KodeverkConsumer -> KodeverkCommand. For å kunne gi de syntetiske personene tilfeldig yrke.
4. Så initialiseres alderspennlisten som er gitt i /service/util/AlderspennList.java hvor alderspennene som kan bli valgt utifra er gitt.
5. Så blir alias tabellen initialisert ved å bruke sannsynlighetsfordeling som er gitt i /service/util/AlderspennList
6. Så blir parameterne hentet fra jobb-parameter db som ett map.
7. Henter antallet organisasjoner fra TenorService.java -> TenorConsumer -> TenorCommand
8. Går igjennom hver organisasjon og henter personer fra pdl, med samme første siffer i postnummer med det gitte aldersintervallet som blir bestemt fra aliastabellen.
9. PdlService vil sjekke om personen ikke er i bruk ved å sjekke om de bare har "TESTNORGE" tag.
10. Velger så ut en tilfeldig person fra de mulige personene og sjekker om denne kan ansettes, ved å se om den kombinerte nye stillingsprosenten <=100. Dette gjøres ved å hente eksisterende arbeidsforhold fra aareg.
11. Hvis personen ikke kan ansettes så fjernes denne personen fra mulige personer og en ny velges.
12. Hvis personen kan ansettes så blir en ny arbeidsavtale oprettet ved å opprette ett arbeidsforhold i aareg via AnsettelsesService.java
13. Så blir personene som er ansatt i denne organisajonen logget til ansettelse-logg db.
14. Så blir skritt 8-13 gjentatt for det riktige antallet personer og organisasjoner.

## Services
TenorService.java
* Henter Organisasjoner og OrganisasjonsOrvesik fra app: tenor-search-service
PdlService
* Henter personer fra pdl gjennom dolly-proxy og bruker graphQL for å hente personer med de hensynene vi trenger.
* Henter også tags i bolk for å sjekke om personene ikke er i bruk (at de bare har "TESTNORGE" taggen)
KodeverkService
* Henter kodeverk som en List<String> som er en liste av kodene fra app: kodeverk-service
JobbService
* Klassen som henter og oppdaterer jobb_parameter db.
ArbeidsforholdService
* Henter og oppretter arbeidsforhold fra aareg igjennom dolly-proxy
AnsettelsesService
* Klassen som inneholder logikken for å ansette mennesker i organisasjoner.
  * Tar hensyn til lokasjon ved at første siffer i postnummeret til personen og organisasjonen skal være likt
  * Tar hensyn til alder ved å å trekke ut ett alderspenn ved å bruke alias metoden
  * Kan ansette mennesker med forskjellig stillingsprosent ved at ansettelsesService sjekker at ikke den totale gjeldende stillingsprosenten er over 100%
* Henter verdiene som skal brukes fra jobb_parameter db
* Logger de som er ansatt i ansettelse_logg db
AnssettelsesLoggService
* Klassen som logger til ansettelses_logg db

### Forklaring av alias metoden
Alias metoden fungerer slik at når man initaliserer alias metoden så vil den lage en alias tabell 
av sannsynlighetsfordelingen som er gitt, denne sannsynlighetsfordeling trenger ikke å være normalisert
siden det er det første metoden gjør. 
Man kan se på alias tabellen slik at hver indeks korresponderer til 1/n av den totale sannsynligheten.
Hvis ett utfall har større sannsynlighet enn dette så må den bli delt opp og fordelt til utfall som har en mindre sannsynlighet.
Se: https://pbr-book.org/4ed/Sampling_Algorithms/The_Alias_Method og https://en.wikipedia.org/wiki/Alias_method
for en mer inngående forklaring.

For å sette opp tabellen har en kompleksitet på O(nlogn), men hvert trekk fra alis tabllen vil ha en kompleksitet av O(1).
Dette betyr at dette er en mer effektiv metode å gjøre trekk fra en sannsynlighets fordeling med distinkte sannsynligheter.

### Sannsynlighetsfordelingen:
Jeg fant disse tallene fra ssb i tabell 1 som vi følte korresponderte til det vi skulle.
https://www.ssb.no/arbeid-og-lonn/sysselsetting/statistikk/antall-arbeidsforhold-og-lonn

|  Alder | <25     | 25-39     | 40-54   | 55-66   | over 67 |
|---|---------|-----------|---------|---------|---------|
| Antall  | 436 106 | 1 022 448 | 976 833 | 563 804 | 72 363  |

### Apper testnav-levende-arbeidsforhold-ansettelse prater med
* dolly-proxy
* tenor-search-service
* kodeverk-service

### Persistent lagring
* testnav-levende-arbeidsforhold-ansettelse:
  * jobb_parameter: Database for å lagre de forskjellige parameterene som kan stilles på, og verdien som er aktiv pluss en text array med mulige verdier.
  * ansettelse_logg: Database for å logge hvem som har blitt ansatt i hvem organisasjon.


## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Lokal PostgreSQL.](../../docs/modules/ROOT/pages/local/local_db.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)
