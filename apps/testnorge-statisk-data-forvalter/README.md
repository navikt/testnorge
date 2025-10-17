---
layout: default
title: Testnorge Statisk Data Forvalter
parent: Applikasjoner
---

# testnorge-statisk-data-forvalter
Statisk data forvalter er en applikasjon som har som ansvar å forvalte faste test-data i NAV. 
Denne applikasjonen belager seg på flere andre applikasjoner for å kunne legge inn data.
 
## Statiske data
Integrasjonen mot TPSF og TPS er fjernet men ikke oppdatert i figur:
![Faste data flowchart](./doc/images/Faste%20data%20flowchart.png "Faste data flowchart")

All data som eksisterer i databasen er test-data som skal kjøres inn i miljøer etter tømming og produksjonslaster.
Av dataen som skal inn i TPS er det bare ekstra identer som ikke eksisterer i TPSF avspillergruppen til SKD-mantallet som
er lagt inn. Resten av mantallet ekisterer der. 

### Syntetisering av manglende data
Manglende data på identer blir løpende syntetisert hvis det ikke er fylt inn i databasen. Dette er gjort for å redusere
kompleksitetsbehovet hos de som ønsker faste data. 

Eksempel: En tester trenger en person med arbeidsforhold i AAREG i en bedrift med orgnr 123674181. 

I dette tilfellet må bedriftsnavn og enhetstype settes, og for en person må mange felter settes, slik som adresse, navn 
osv. Disse verdiene blir automatisk satt av applikasjonene som er gitt som avhengigheter. 
 
## API
Apiet inneholder endepunkter for å individuelt legge inn all statisk data i et miljø gitt hvilken type (SAM, TP, SKD, EREG).
Swagger finnes under [/swagger](https://testnorge-statisk-data-forvalter.intern.dev.nav.no/swagger) -endepunktet til applikasjonen.

For å legge til data kan man enten gå rett på databasen og fylle ut feltene, eller bruke APIet. 

Data fra CSV-fil legges inn i databasen via file-controller. For riktig formatering av æøå konverteres excel-fil til UTF-8 
i Notepad++ (Encoding -> Convert to UTF-8 (eventuelt "uten BOM") før den brukes i POST. På samme måte må fil fra GET 
konverteres til ANSI for å vise korrekte tegn i Excel.   


## Nytt domene
For å opprette et nytt domene må en POJO og repository opprettes. Det er sterkt anbefalt å eksponere dette nye domenet i APIet.

Nåværende domener belager seg på eksisterende applikasjoner som ikke er direkte tilknyttet fast data. Denne modellen er også
sterkt anbefalt å følge. Disse applikasjonen er ansvarlige for å opprette manglende data.


## Lokal kjøring
* [Generelt.](../../docs/local_general.md)
* [Secret Manager.](../../docs/local_secretmanager.md)

