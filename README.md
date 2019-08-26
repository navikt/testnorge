# testnorge-statisk-data-forvalter

Statisk data forvalter er en applikasjon som har som ansvar å forvalte faste test-data i NAV. 
Denne applikasjonen belager seg på flere andre applikasjoner for å kunne legge inn data. 

## Avhengigheter

De følgende applikasjonene må være kjørende for å kunne legge inn all data i et miljø. 

 - [Ereg flatfil mapper](https://github.com/navikt/testnorge-ereg-mapper)
 - [TP](https://stash.adeo.no/projects/FEL/repos/testnorge-tp/browse) for å legge inn i TP databasen, skal ikke være 
 nødvendig, men denne gjør det raskere enn selve TP applikasjonen som lytter på distribusjonsmeldinger fra TPS 
 - [SKD adapter](https://stash.adeo.no/projects/FEL/repos/testnorge-skd/browse) for oppretting av testpersoner i TPS
 - [SAM](https://stash.adeo.no/projects/FEL/repos/testnorge-sam/browse) Samme som TP for SAM
 
## API

Apiet inneholder endepunkter for å individuelt legge inn all statisk data i et miljø gitt hvilken type (SAM, TP, SKD, EREG).
Dokumentasjon: [Swagger](https://testnorge-statisk-data-forvalter.nais.preprod.local/swagger-ui.html)

For å legge til data kan man enten gå rett på databasen og fylle ut feltene, eller bruke APIet. 

## Nytt domene

For å opprette et nytt domene må en POJO og repository opprettes. Det er sterkt anbefalt å eksponere dette nye domenet i APIet.
Nåværende domener belager seg på eksisterende applikasjoner som ikke er direkte tilknyttet fast data. Denne modellen er også
sterkt anbefalt å følge. 
