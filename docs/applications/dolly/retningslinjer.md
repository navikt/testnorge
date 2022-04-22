---
title: Retningslinjer for bruk av testdata i NAV
layout: default
parent: Dolly
---

# {{ page.title }}

## Hensikt, målgrupper og ansvar

Syntetiske testdata skal brukes i utvikling og forvaltning av NAVs IT-løsninger. Ved unntaksvis bruk av produksjonsdata, gjelder spesielle sikkerhetstiltak. Disse føringene er sentrale for å minimere og sikre kontroll med bruken av persondata:

- **Syntetiske testdata** kan ikke kobles til reelle personer, og er ikke personopplysninger
- **Produksjonsdata** kan direkte eller indirekte knyttes til en enkeltperson, og må håndteres som personopplysninger som må beskyttes gjennom tilgangskontroll, maskering, logging m.m.

Retningslinjene gjelder for alle ansatte og eksterne som er involvert i utvikling av NAVs IT-løsninger. Følgende har et spesielt ansvar:

- _Teamleder i produktteam_ har ansvaret for at retningslinjene følges av interne og eksterne ressurser i teamet.
- _IT Data og Innsikt_ har ansvaret for basisløsninger for syntetiske testdata
- _Sikkerhetsseksjonen_ har overordnet ansvar for bruk av testdata og informasjonssikkerhet

Retningslinjene er operative rutiner til Styringssystem for både Sikkerhet og Personvern. Sikkerhetsseksjonen har ansvaret for vedlikehold, og endringsbehov kan meldes dit.

_Avgrensning for denne versjonen, vinter 2019:_

- _NAV utvikler løsning for syntetiske testdata i 2018 - 2019, og syntetiske data blir gradvis tilgjengelig pr område/system. For områder der syntetiske data ikke er tilgjengelig, vil sikkerhetsregler for unntak gjelde._
- _Ny løsning for tilgangskontroll ved unntaksvis bruk av produksjonsdata vurderes av Personvernprosjektet_

---

## Bruk av syntetiske testdata i NAV

Syntetiske testdata skal brukes til utvikling og forvaltning, og er tilgjengelig som:

- "Mini-Norge" - med egenskaper lik normalbefolkningen - er lagt ut i syntetiske utviklings-/testmiljøer.
- Egendefinerte data - fra Dolly selvbetjeningsløsning - for randtilfeller og spesielle testbehov. Dolly sikrer at personer, egenskaper m.m. er syntetiske.
- Syntetiske datasett for bruk i integrasjonstest med eksterne samhandlere
- Tekniske stubber som simulerer eksterne kilder brukes til volum- og basistesting

Syntetiske testdata er ikke personopplysninger og kan brukes uten sikkerhetstiltak.

Det gjennomføres tekniske og manuelle kvalitetskontroller for å sikre at det ikke kommer reelle data inn i syntetiske miljøer, og at syntetiske data ikke kan forveksles med reelle data:

- Navnepool - for å opprette fiktive personer - med navn som ikke kan forveksles med reelle navn
- Identpool - for å opprette identer som imiterer fødselsnummer og D-nummer. Identpoolen tar en teknisk sjekk mot reelle fødselsnummer og D-nummer i Folkeregisteret
- Teknisk sjekk av syntetiske miljøer gjennomføres jevnlig for å sjekke at det kun brukes fiktive navn og identer
- Testdata fra eksterne samhandlere sjekkes gjennom manuelle stikkprøver. Frekvens og omfang tilpasses behov og risiko for det aktuelle området.
- Retningslinjer for miljøoppsett gir føringer som skal hindre krysskobling mellom syntetiske miljøer og miljøer med reelle data

---

## Prinsipper for å lage egne testdata i Dolly selvbetjening

Dolly selvbetjening brukes for å lage egne data til randtilfeller og spesialbehov. Syntetiske personer gis egenskaper fra registre og fagsystemer - f.eks. sivilstand, inntekt, statsborgerskap.
Brukere av Dolly selvbetjening har selv ansvar for å følge følgende prinsipper ved oppretting av syntetiske data:

- Dolly tilbyr et stort utvalg attributter som kan settes på en person. Selv om det kun er syntetiske personer som blir laget i Dolly, finnes det allikevel en risiko for å skape gjenkjennbare personer, ved å kombinere verdier som er svært spesifikke for en ekte person. Derfor må du aldri ta utgangspunkt i reelle personer når du oppretter syntetiske personer - alle verdier som settes for å dekke behovet må være tilfeldig valgt.
- Når du er logget inn i Dolly har du tilgang til alle brukeres grupper og personer. Ikke gjør endringer på eller slett andres grupper eller personer uten at dette er avtalt med eier.
- Dolly selvbetjening har personlig innlogging med Azure AD. Ikke del påloggingsinformasjonen din med andre brukere. Brukere som mangler tilgang til Dolly bes kontakte teamet for å få hjelp til å ordne dette.
- Snakk med oss! Skulle det være noe, vil vi gjerne at du kontakter oss på #dolly på Slack. Innspill, ønsker, meldinger om feil, osv. hjelper oss med å gjøre Dolly bedre, og kommer alle brukerne til gode.

---

## Unntak og sikkerhetstiltak ved bruk av produksjonsdata

Unntaksvis må det brukes produksjonsdata. For å redusere risiko knyttet til bruk av reelle persondata, gjelder spesielle sikkerhetstiltak.

### Unntak fra bruk av syntetiske testdata

Unntak fra regelen om syntetiske data gjelder for oppgaver som bare kan løses ved bruk av produksjonsdata. Unntakene vurderes pr produktteam i samarbeid med behandlingsansvarlig og vil typisk gjelde:

- Kritiske feil i produksjon der produksjonsdata er nødvendig for rekonstruksjon av feil
- Korrigering av datakvalitet
- Spesialtester/engangstilfeller der kompliserte og sammensatte verdikjeder endres samtidig og må verifiseres på tvers
- Større datakonvertering
- Systemet/området skal saneres eller moderniseres i nær framtid. Unntak/dispensasjon skal være behandlet og spesifisert pr område i samarbeid med behandlingsansvarlig og ihht felles vurderingskriterier. \*

### Oversikt over sikkerhetstiltak ved bruk av produksjonsdata

Produktteamet har ansvaret for sikkerhetstiltak for egne områder/systemer ihht oversikt under:

**Begrensning i kopiering og bruk av produksjonsdata**

- Brukere med diskresjonskode 6 og 7 skal alltid fjernes ved kopi/uttrekk fra produksjon.
- Brukere ansatt i NAV skal alltid fjernes ved kopi/uttrekk fra produksjon.
- For kopi/uttrekk fra Bisys skal brukere bosatt i utlandet og dekket av "paragraf 19" (tilsvarer kode 6 og 7), tas ut manuelt
- Maskering av data brukes i en overgangsperiode der det fins løsninger for dette.

Produktteamets ansvar:

- Teamets applikasjoner skal ha teknisk støtte for å fjerne kode 6/7 og brukere ansatt i NAV. Databaseansvarlig for det aktuelle området sørger for at scriptene for fjerning blir kjørt når skyggemiljøer oppdateres.
- Teamet skal ha felles rutiner for når og hvordan persondata maskeres - i overgangsperiode før syntetiske data er tilgjengelig.
- Teamet skal ha har felles rutiner som sikrer at teamets medlemmer ikke bruker data om seg selv eller egen familie

**Midlertidig lagring og tilgangskontroll til ekstraordinære produksjonskopier**

Det er ekstraordinært behov for kopi av produksjonsdata til feilanalyse og korrigering av datakvalitet

- Kopiering/uttrekk fra produksjon utføres av ressurser i IT som har dette som en fast oppgave - med tilgangskontroll og logging ihht rutiner for produksjon.
- Kopier fra databaser lagres på egne områder - og tilgang gis til enkeltpersoner av databaseansvarlig. Tilgang og lagring er tidsbegrenset.
- Kopier som tas ut som filer, lagres i filarkiv med tilgangskontroll - for en tidsbegrenset periode
- Hvis kopier - ved kritiske hendelser - distribueres pr epost, på minnepinne o.l. , skal innholdet beskyttes gjennom kryptering og bruk av passord. Innhold/data skal avgrenses til det som er strengt tatt nødvendig. jf operative retningslinjer for Informasjonssikkerhet
- Støttemateriell for analyse og test skal ikke inneholde personinformasjon, f.eks testscripts og tilhørende dokumentasjon

Produktteamets ansvar:

- Behov for produksjonskopier med personopplysninger skal vurderes for hver enkelt sak/uttrekk - i samarbeid med behandlingsansvarlig
- Teamet bestiller kopi/uttrekk fra produksjon fra ressurser i IT som har dette som en fast/definert oppgave.
- Teamet skal ha felles filarkiv for produksjonskopier. Lagring skal tidsbegrenses. Normalt vil en maksgrense på 30 dager være dekkende.
- Teamleder gir fast tilgang til filarkiv for ressurser som har oppgaver knyttet til håndtering av kritiske feil og andre hendelser som krever umiddelbar bruk av arkivet. Ressurser som har behov for å løses enkeltoppgaver, gis midlertidig tilgang.

**Tilgangskontroll og logging - i skyggemiljøer og spesialmiljøer med produksjonsdata**

Skyggemiljøer er produksjonslike miljøer som brukes til bestemte formål. Tjenestlige behov og roller /oppgaver gir rammer for tilganger og bruk av skyggemiljøene:

- Fagsystemtilganger gis som testbrukere og styres gjennom eget identadministrativ verktøy - IDA
- Systemtilganger (oppslag i database m.m) - følger NAVs tilgangsrutiner for produksjon
  Det lages logger for både fagsystem/IDA-brukere og systembrukere . For lagring av logger, gjelder samme regler som for produksjonsmiljøer.

Produktteamets ansvar:

- Teamleder gir føringer for teamets bruk av faste og midlertidige tilganger til skyggemiljøer - for teamets medlemmer.
- Teamleder skal ha oversikt over hvem som har tilgang til miljøer med produksjonsdata.
- Signerte taushetserklæringer gjelder generelt for alle i teamet.

**Kontroll av data fra eksterne samhandlere**

Integrasjonstester med eksterne samhandlere skal kun baseres på syntetiske testdata. Andre tester skal baseres på syntetiske stubber som simulerer eksterne kilder.
For å hindre at reelle data fra eksterne samhandlere blandes sammen med syntetiske data, skal det gjennomføres jevnlige kontroller.

Produktteamets ansvar:

- Teamet skal ha avtaler med aktuelle eksterne samhandlere om bruk av miljøer og testdata. Det skal ikke testes på tvers uten syntetiske testdata.
- Teamet skal ha faste datasett til bruk for integrasjonstester med faste eksterne samhandlere. Dette gjelder også for løsninger der NAV er dataforvalter på vegne av andre virksomheter (f.eks Helsedirektoratet). Datasett kan deles med samhandlerne for test fra deres side.
- Teamet skal gjennomføre jevnlig kontroll/stikkprøver av data fra eksterne samhandlere for å sikre at det ikke brukes reelle data.

_\* Personvernprosjektet vil i samarbeid med produktteamene vurdere unntak/dispensasjon for bestemte systemer/områder som del av arbeidet med syntetiske testdata. Sikkerhetsseksjonen og Juridisk seksjon konsulteres ved eventuell usikkerhet._

---

## Håndtering av hendelser og avvik

Avvik, dvs hendelser som bryter regler for bruk av testdata eller sikkerhetstiltak, skal håndteres ihht etablerte prosesser:

1. Prosess for håndtering av hendelser
2. Brudd på informasjonssikkerhet og personvern meldes i tillegg i ASYS (system for avviksrapportering) - for videre oppfølging av Sikkerhetsseksjonen og Personvernombudet.

---

