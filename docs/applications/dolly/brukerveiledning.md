---
title: Brukerveiledning
layout: default
parent: Dolly
nav_order: 1
---

# {{ page.title }}
{: .no_toc }

## Innhold
{: .no_toc .text-delta }

1. TOC
{:toc}

---

Dolly er NAVs selvbetjeningsløsning for å opprette syntetiske data. I Dolly kan du opprette syntetiske 
personer med forskjellige egenskaper, og tilgjengeliggjøre dataene i valgte testmiljøer.

Link til Dolly: [Dolly](https://dolly.ekstern.dev.nav.no/)

---

## Dolly pålogging

Dolly benytter seg nå av Single Sign On (SSO) som fører til at du blir innlogget gjennom dette systemet.
Du vil derfor som oftest være logget inn på din bruker med en gang du åpner Dolly.

Det første som møter deg når du har logget inn er en startside med noen menyvalg. Siden åpnes automatisk med en oversikt over dine grupper

![Startside](assets/startside.png)

Noen ganger vil du bli møtt av en eller flere meldinger når du åpner Dolly, f.eks. om det er første gang du bruker applikasjonen, eller om det har dukket opp nyheter.

Det er viktig at du leser disse meldingene, da de inneholder nyttig informasjon om bruk av Dolly.

![Varslinger](assets/varslinger.png)

---

## Opprette en ny gruppe

For å opprette nye syntetiske personer i Dolly, så kan du enten lage en ny gruppe eller legge til personer i en eksisterende gruppe. En gruppe inneholder personer du/teamet har opprettet.

Under ‘Mine’ vises grupper opprettet av deg selv. Under ‘Alle’ vises dine og andres grupper. En god regel er at du ikke endrer andres grupper uten avtale med eieren.

Trykk på "ny gruppe" og velg navn og beskriv gjerne hensikten med gruppen. Når hensikten er beskrevet er det enklere for både deg selv og eventuelt andre du samarbeider med å forstå hva denne gruppen brukes til. Trykk "Opprett og gå til gruppe".

![Testdatagrupper](assets/testdatagrupper.png)

Inne på gruppen har du en oversikt over alle personene som tilhører gruppen og hvilke egenskaper de har. Her har du også mulighet til å legge til nye personer.

![Testdatagruppe](assets/ny_testdatagruppe.png)

Det kommer opp en boks der du får to valg:

1. Opprette helt nye personer. Da finner Dolly et ubrukt fnr, dnr eller bost.
2. Opprette fra eksisterende fnr, dnr eller Bost (identifikasjonsnummer kan ikke finnes i prod eller i Dolly).

Vi velger å opprette nye og angir at vi ønsker 10 personer i bestillingen.

Opprettelse av personer følger en prosess med tre steg:

![Opprettelse prossess](assets/opprettelse_prossess.png)

1. Først må du velge hvilke egenskaper du selv ønsker å bestemme. De resterende egenskapene vil systemet tilegne for deg
2. Deretter får du mulighet til å angi verdier på egenskapene du valgte i steg 1
3. Til slutt får du en oppsummering og mulighet til å velge hvilke(t) testmiljø personene skal sendes til

### Velg egenskaper

![Velg egenskaper](assets/velg_egenskaper.png)

Som du ser av skjermbildet over, så er det en lang rekke områder med egenskaper du kan velge. Trykk på pil ned til høyre i boksen for å ekspandere hvert område. I de fleste tilfellene er det kun noen utvalgte egenskaper som er nødvendig å definere for det eller de testscenariene du skal gjennomføre.
For at du skal slippe å velge alle egenskaper som trengs for å få en komplett person vil Dolly tildele verdier til de egenskapene som ikke er avgjørende for din test, altså de du ikke krysser av for i skjermbildet.

I dette eksemplet ønsker vi å teste endringer i arbeidsforhold og krysser av for følgende egenskaper:

![Valgt egenskap](assets/valgt_egenskap.png)

Team Dolly jobber kontinuerlig med videreutvikling av Dolly og nye egenskaper legges til fortløpende. Om det er noe du savner, så meld det inn på [#Dolly](https://nav-it.slack.com/archives/CA3P9NGA2) på Slack.

Når du har angitt aktuelle verdier for egenskapene trykk videre og i neste steg får du mulighet til å velge hvilke(t) miljø personene skal opprettes i.

For mer informasjon om NAVs testmiljøer se: [Miljøer](https://confluence.adeo.no/pages/viewpage.action?pageId=305341700)

![Miljø valg](assets/miljoe_valg.png)

Velg miljø og trykk opprett

![Opprettet gruppe](assets/opprettet_gruppe.png)

Gruppen har fått nye personer og du kan jobbe med disse i relevante fagsystemer. Trykk på pil ned til høyre på raden for å få opp detaljinformasjon om hver enkelt person.

### Legg til / endre person
Noen ganger er det ønskelig å legge til ekstra egenskaper på en person man allerede har opprettet. Dette kan gjøres
ved å først åpne opp detaljinformasjonen for personen du ønsker å legge til på. Deretter trykker du på "LEGG TIL/ENDRE"-knappen
og velg så hvilke ekstra egenskaper personen skal ha. 

### Demo
#### Opprette personer med tilfeldige verdier
Videoen nedenfor viser hvordan man kan opprette personer med tilfeldige verdier.

<video src="https://user-images.githubusercontent.com/58416744/160127199-77556648-6be9-44b6-b7ca-df4d4ae52a7d.mov"
controls="controls" style="max-width: 730px;" >
</video>

#### Opprette person med ønsket verdier
Videoen nedenfor viser hvordan man kan opprette en person med ønskede verdier.

<video src="https://user-images.githubusercontent.com/58416744/160127595-4655a2d6-9a59-4f56-b231-87fb4cded2c9.mov"
controls="controls" style="max-width: 730px;" >
</video>

#### Legg til/endre person
Videoen nedenfor viser hvordan man kan legge til ekstra egenskaper på en person. 

<video src="https://user-images.githubusercontent.com/58416744/160127725-8e96934c-af19-4801-b69d-67fe907b16d7.mov"
controls="controls" style="max-width: 730px;">
</video>


---

## Opprette og bruke maler

Når du oppretter en ny person i Dolly kan du velge å lagre denne bestillingen som en mal. Denne malen kan senere hentes opp av deg selv eller andre, og vil gi deg en ferdigutfylt bestilling med de samme egenskapene og verdiene.

For å lage en mal av en bestilling huker du av for «Lagre som mal» på siste steg i bestillingsprosessen. Gi malen et beskrivende navn, så den er lett å finne tilbake til senere.

![Opprtte mal](assets/opprette_mal.png)

For å bruke en mal, velger du dette i første steg når du oppretter personer. Huk av for «Maler», og velg om du ønsker å bruke en av dine egne maler eller en annen brukers mal. Du vil da få opp en liste med alle malene denne brukeren har laget. Det vil for eksempel si at om en person på teamet ditt har laget en mal du ønsker å bruke, velger du først personens brukernavn og deretter malen du ønsker å bruke.

![Bruk mal](assets/bruk_mal.png)

Når du da starter bestillingen vil du få de valgte egenskapene og verdiene som utgangspunkt, men du har også muligheten til å endre på disse dersom du ønsker det.

### Demo
Videoen nedenfor viser hvordan man kan opprette og ta i bruk maler. 

<video src="https://user-images.githubusercontent.com/58416744/160128266-a13c18ea-a709-4914-8627-befd25e1af16.mov" 
       controls="controls" style="max-width: 730px;" >
</video>

---

## Endringsmelding

Øverst i menyen kan du velge endringsmelding. Her har du mulighet til å sende inn en fødselsmelding eller dødsmelding til et ønsket testmiljø.
Endringsmeldinger er en egen applikasjon separat fra Dolly. Dette medfører at man trenger spesifikk tilgang for å  kunne sende endringsmeldinger - ta kontakt med Team Dolly dersom du har behov for dette.

Merk at det kun er mulig å sende inn endringsmelding på en person om gangen.

!!Denne er udatert og vil bli fjernet i nær fremtid!!

Det vil fortsatt være mulig å sende fødselsmeldinger og dødsmeldinger i Dolly:

* Sende fødselsmelding: Gå til gruppe og finn personen som skal være forelder. Her finnes to alternativer:
    * Velg "Legg til/endre", og huk av for "Barn" i første steg. I neste steg kan et utvalg av egenskaper velges for barnet.
    * Hvis barnet (og eventuelt den andre forelderen) allerede er opprettet i gruppen: Velg "Legg til relasjoner" og legg til barn.

* Sende dødsmelding:
    * Gå til gruppe og finn personen det skal sendes dødsmelding på.
    * Velg "Legg til/endre", og huk av for "Dødsdato" i første steg.
    * I neste steg kan dødsdatoen settes, før dødsmeldingen sendes som en vanlig bestilling.

  For å sende dødsmelding på personens partner/barn må partner eller barn hukes av i første steg. Da vil det være mulig å sette dødsdato på eksisterende relasjoener i steg to.

For personer som ikke eksisterer i Dolly må disse først hentes inn ved å opprette person, velge "Eksisterende person" og skrive inn ident.

![Endringsmelding](assets/endringsmelding.png)

---

## API-dok

Øverst i menyen ligger også en lenke til API dokumentasjon. Den tar deg til Swagger og dokumentasjon av tilgjengelige APIer.

![API dok](assets/api_dok.png)

---

## Feil ved innlogging

![Tvungen utlogging](assets/logged_out_error.png)

Dersom du har gjentatte problemer med å få logget inn (eller du blir konstant logget ut) kan det være et problem med 
cookies på nettsiden. Det er to ting du kan gjøre for å prøve for å fikse problemet:

* Logg ut manuelt:
    * Du kan manuelt logge ut av Dolly ved å trykke på den følgende linken: https://dolly.intern.dev.nav.no/oauth2/logout
* Clear cookies i nettleser:
  * Nedenfor finner du en demo-video for hvordan dette kan gjøres. 

<video src="https://user-images.githubusercontent.com/58416744/159910685-f4bcbe86-c856-459c-a220-b242c46a59cd.mov"
       controls="controls" style="max-width: 730px;">
</video>


