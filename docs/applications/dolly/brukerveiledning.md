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

Dolly er NAVs selvbetjeningsløsning for å opprette syntetiske testdata.  I Dolly kan du opprette syntetiske testpersoner med forskjellige egenskaper, og tilgjengeliggjøre testdataene i valgte testmiljøer.

Fra enhet som er satt opp med Naisdevice:

[dolly.ekstern.dev.nav.no/](https://dolly.ekstern.dev.nav.no/)

Utenfor de interne Nav systemene benytt:

[dolly.nais-dev-fss.adeo.no](https://dolly.nais-dev-fss.adeo.no/)

Denne lenken krever tilgangen "Utvikling på laptop"

---

## Dolly pålogging

Dolly benytter seg nå av Single Sign On (SSO) som fører til at du blir innlogget gjennom dette systemet.
Du vil derfor som oftest være logget inn på din bruker med en gang du åpner Dolly.

Det første som møter deg når du har logget inn er en startside med noen menyvalg. Siden åpnes automatisk med en oversikt over dine testdatagrupper

![Startside](assets/startside.png)

Noen ganger vil du bli møtt av en eller flere meldinger når du åpner Dolly, f.eks. om det er første gang du bruker applikasjonen, eller om det har dukket opp nyheter.

Det er viktig at du leser disse meldingene, da de inneholder nyttig informasjon om bruk av Dolly.

![Varslinger](assets/varslinger.png)

---

## Opprette en ny testdatagruppe

For å opprette nye syntetiske testpersoner i Dolly, så kan du enten lage en ny testdatagruppe eller legge til testpersoner i en eksisterende gruppe. En testdatagruppe inneholder testpersoner du/teamet har opprettet.

Under ‘Mine’ vises testdatagrupper opprettet av deg selv. Under ‘Alle’ vises dine og andres testdatagrupper. En god regel er at du ikke endrer andres testdatagrupper uten avtale med eieren.

Trykk på "ny gruppe" og velg navn og beskriv gjerne hensikten med gruppen. Når hensikten er beskrevet er det enklere for både deg selv og eventuelt andre du samarbeider med å forstå hva denne testdatagruppen brukes til. Trykk "Opprett og gå til gruppe".

![Testdatagrupper](assets/testdatagrupper.png)

Inne på gruppen har du en oversikt over alle testpersonene som tilhører gruppen og hvilke egenskaper de har. Her har du også mulighet til å legge til nye testpersoner.

![Testdatagruppe](assets/ny_testdatagruppe.png)

Det kommer opp en boks der du får to valg:

1. Opprette helt nye testpersoner. Da finner Dolly et ubrukt fnr, dnr eller bost.
2. Opprette fra eksisterende fnr, dnr eller Bost (identifikasjonsnummer kan ikke finnes i prod eller i Dolly).

Vi velger å opprette nye og angir at vi ønsker 10 personer i bestillingen.

Opprettelse av testpersoner følger en prosess med tre steg:

![Opprettelse prossess](assets/opprettelse_prossess.png)

1. Først må du velge hvilke egenskaper du selv ønsker å bestemme. De resterende egenskapene vil systemet tilegne for deg
2. Deretter får du mulighet til å angi verdier på egenskapene du valgte i steg 1
3. Til slutt får du en oppsummering og mulighet til å velge hvilke(t) testmiljø testpersonene skal sendes til

### Velg egenskaper

![Velg egenskaper](assets/velg_egenskaper.png)

Som du ser av skjermbildet over, så er det en lang rekke områder med egenskaper du kan velge. Trykk på pil ned til høyre i boksen for å ekspandere hvert område. I de fleste tilfellene er det kun noen utvalgte egenskaper som er nødvendig å definere for det eller de testscenariene du skal gjennomføre.
For at du skal slippe å velge alle egenskaper som trengs for å få en komplett testperson vil Dolly tildele verdier til de egenskapene som ikke er avgjørende for din test, altså de du ikke krysser av for i skjermbildet.

I dette eksemplet ønsker vi å teste endringer i arbeidsforhold og krysser av for følgende egenskaper:

![Valgt egenskap](assets/valgt_egenskap.png)

Team Dolly jobber kontinuerlig med videreutvikling av Dolly og nye egenskaper legges til fortløpende. Om det er noe du savner, så meld det inn på [#Dolly](https://nav-it.slack.com/archives/CA3P9NGA2) på Slack.

Når du har angitt aktuelle verdier for egenskapene trykk videre og i neste steg får du mulighet til å velge hvilke(t) miljø testpersonene skal opprettes i.

For mer informasjon om NAVs testmiljøer se: [Miljøer](https://confluence.adeo.no/pages/viewpage.action?pageId=305341700)

![Miljø valg](assets/miljoe_valg.png)

Velg miljø og trykk opprett

![Opprettet gruppe](assets/opprettet_gruppe.png)

Testdatagruppen har fått nye testpersoner og du kan jobbe med disse i relevante fagsystemer.  Trykk på pil ned til høyre på raden for å få opp detaljinformasjon om hver enkelt testperson.

---

## Opprette og bruke maler

Når du oppretter en ny person i Dolly kan du velge å lagre denne bestillingen som en mal. Denne malen kan senere hentes opp av deg selv eller andre, og vil gi deg en ferdigutfylt bestilling med de samme egenskapene og verdiene.

For å lage en mal av en bestilling huker du av for «Lagre som mal» på siste steg i bestillingsprosessen. Gi malen et beskrivende navn, så den er lett å finne tilbake til senere.

![Opprtte mal](assets/opprette_mal.png)

For å bruke en mal, velger du dette i første steg når du oppretter personer. Huk av for «Maler», og velg om du ønsker å bruke en av dine egne maler eller en annen brukers mal. Du vil da få opp en liste med alle malene denne brukeren har laget. Det vil for eksempel si at om en person på teamet ditt har laget en mal du ønsker å bruke, velger du først personens brukernavn og deretter malen du ønsker å bruke.

![Bruk mal](assets/bruk_mal.png)

Når du da starter bestillingen vil du få de valgte egenskapene og verdiene som utgangspunkt, men du har også muligheten til å endre på disse dersom du ønsker det.

---

## Endringsmelding

Øverst i menyen kan du velge endringsmelding. Her har du mulighet til å sende inn en fødselsmelding eller dødsmelding til et ønsket testmiljø.

Merk at det kun er mulig å sende inn endringsmelding på en person om gangen.

!!Denne er udatert og vil bli fjernet i nær fremtid!!

Det vil fortsatt være mulig å sende fødselsmeldinger og dødsmeldinger i Dolly:

* Sende fødselsmelding: Gå til testdatagruppe og finn personen som skal være forelder. Her finnes to alternativer:
    * Velg "Legg til/endre", og huk av for "Barn" i første steg. I neste steg kan et utvalg av egenskaper velges for barnet.
    * Hvis barnet (og eventuelt den andre forelderen) allerede er opprettet i testdatagruppen: Velg "Legg til relasjoner" og legg til barn.

* Sende dødsmelding:
    * Gå til testdatagruppe og finn personen det skal sendes dødsmelding på.
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

## Importere grupper fra gammel Z-bruker

Dersom du tidligere har brukt Dolly med en Z-bruker, vil du se at testdatagruppe-oversikten er tom første gang du logger deg på med personlig brukerkonto.

Ikke bekymre deg - testpersonene dine er ikke slettet! Men fordi de er koblet til Z-brukeren din må du importere dem til din personlige brukerkonto for å få tilgang til dem.

Import fra Z-bruker kan du gjøre direkte fra testdatagruppe-oversikten, eller fra Min side.

![Import fra min side](assets/minside.png)

For at overgangen skal gå så smertefritt som mulig, vil følgende regler gjelde ved import av Z-bruker:
* Du kan importere så mange Z-brukere du ønsker. Har du flere Z-brukere vil altså alle dataene fra disse kunne samles på din personlige brukerkonto.
* Hver Z-bruker kan kun importeres av én personlig brukerkonto. Her er det førstemann til mølla-prinsippet som gjelder, dvs. at når du importerer en Z-bruker vil ingen andre kunne importere den samme.

Det vil fortsatt være mulig å sette andre brukeres testdatagrupper som “favoritter”, slik at du har enkel tilgang til dem fra testdatagruppe-oversikten.


## Feil ved innlogging

![Tvungen utlogging](assets/logged_out_error.png)

Dersom du har gjentatte problemer med å få logget inn (eller du blir konstant logget ut) kan det være et problem med 
cookies på nettsiden. Dette er noe vi prøver å finne en løsning for, men imens anbefaler vi våre brukere å prøve å 
cleare cookies når dette skjer. Nedenfor finner du en demo-video for hvordan dette kan gjøres. 

<video src="https://user-images.githubusercontent.com/58416744/159910685-f4bcbe86-c856-459c-a220-b242c46a59cd.mov"
       controls="controls" style="max-width: 730px;"/>


