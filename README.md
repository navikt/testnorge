# testnorge-statisk-data-forvalter

Statisk data forvalter er en applikasjon som har som ansvar å forvalte faste test-data i NAV. 
Denne applikasjonen belager seg på flere andre applikasjoner for å kunne legge inn data. 

## Varighet og varslinger
For å unngå at databasen legger inn utdaterte og ubrukte data etter hver eneste produksjonslast vil faste testdata ha en default varighet på 1 år etter opprettelse. Når disse dataene er på vei til å gå ut vil det bli gitt en varsling på Slack kanalen som ble lagt til med teamet som bestilte dataen. Hvis denne kanalen er fjernet eller slettet vil det bli sendt en mail til mail adressen som ble lagt til. 
### Fornying av gyldighetsperiode
For å fornye perioden må teamene ta kontakt med administratoren av faste testdata som for nå er Orkestrator teamet. Pr. 23.10.2019 kan teamet bli nådd på #Dolly.


## Avhengigheter

De følgende applikasjonene må være kjørende for å kunne legge inn all data i et miljø. 

 - [Ereg flatfil mapper](https://github.com/navikt/testnorge-ereg-mapper) generering av flatfiler som erstatter manuell oppretting
 - [TP](https://stash.adeo.no/projects/FEL/repos/testnorge-tp/browse) for å legge inn i TP databasen, skal ikke være 
 nødvendig, men denne gjør det raskere enn selve TP applikasjonen som lytter på distribusjonsmeldinger fra TPS 
 - [SKD adapter](https://stash.adeo.no/projects/FEL/repos/testnorge-skd/browse) for oppretting av testpersoner i TPS
 - [SAM](https://stash.adeo.no/projects/FEL/repos/testnorge-sam/browse) Samme som TP for SAM
 - [AAreg](https://stash.adeo.no/projects/FEL/repos/testnorge-aareg/browse) for arbeidsforhold
 
## Statiske data

![Faste data flowchart](./doc/images/Faste%20data%20flowchart.png "Faste data flowchart")


All data som eksisterer i databasen er test-data som skal kjøres inn i miljøer etter tømming og produksjonslaster.
Av dataen som skal inn i TPS er det bare ekstra identer som ikke eksisterer i TPSF avspillergruppen til SKD-mantallet som
er lagt inn. Resten av mantallet ekisterer der. 

### Syntetisering av manglende data
Manglende data på identer blir løpende syntetisert hvis det ikke er fylt inn i databasen. Dette er gjort for å redusere
kompleksitetsbehovet hos de som ønsker faste data. 

Eksempel: En tester trenger en person med arbeidsforhold i AAREG i en bedrift med orgnr 123674181. 

I dette tilfellet må bedriftsnavn og enhetstype settes, og for en person må mange felter settes, slik som adresse, navn osv. Disse verdiene blir automatisk satt av applikasjonene som er gitt som avhengigheter. 
 
## API

Apiet inneholder endepunkter for å individuelt legge inn all statisk data i et miljø gitt hvilken type (SAM, TP, SKD, EREG).
Dokumentasjon: [Swagger](https://testnorge-statisk-data-forvalter.nais.preprod.local/swagger-ui.html)

For å legge til data kan man enten gå rett på databasen og fylle ut feltene, eller bruke APIet. 

## Nytt domene

For å opprette et nytt domene må en POJO og repository opprettes. Det er sterkt anbefalt å eksponere dette nye domenet i APIet.

Nåværende domener belager seg på eksisterende applikasjoner som ikke er direkte tilknyttet fast data. Denne modellen er også
sterkt anbefalt å følge. Disse applikasjonen er ansvarlige for å opprette manglende data og gjøre disse tilgjengelige for søk i hodejegeren. 


## Bygg og deploy

CircleCI håndterer bygg og deploy til nais via [deployment-cli](https://github.com/navikt/deployment-cli). Github packages er brukt for å hoste docker image som blir generert og et personlig access token + brukernavn må legges til i CircleCI prosjektet. 
