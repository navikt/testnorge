---
layout: default
title: Testnorge Statisk Data Forvalter
parent: Applikasjoner
---

# testnorge-statisk-data-forvalter

Statisk data forvalter er en applikasjon som har som ansvar å forvalte faste test-data i NAV. 
Denne applikasjonen belager seg på flere andre applikasjoner for å kunne legge inn data. 

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

Data fra CSV-fil legges inn i databasen via file-controller. For riktig formatering av æøå konverteres excel-fil til UTF-8 i Notepad++ (Encoding -> Convert to UTF-8 (eventuelt "uten BOM") før den brukes i POST. På samme måte må fil fra GET konverteres til ANSI for å vise korrekte tegn i Excel.   


## Nytt domene

For å opprette et nytt domene må en POJO og repository opprettes. Det er sterkt anbefalt å eksponere dette nye domenet i APIet.

Nåværende domener belager seg på eksisterende applikasjoner som ikke er direkte tilknyttet fast data. Denne modellen er også
sterkt anbefalt å følge. Disse applikasjonen er ansvarlige for å opprette manglende data og gjøre disse tilgjengelige for søk i hodejegeren. 


## Bygg og deploy

~~CircleCI håndterer bygg og deploy til nais via [deployment-cli](https://github.com/navikt/deployment-cli). Github packages er brukt for å hoste docker image som blir generert og et personlig access token + brukernavn må legges til i CircleCI prosjektet. ~~

## Swagger
Swagger finnes under [/api](https://testnorge-statisk-data-forvalter.nais.preprod.local/api) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør ApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
