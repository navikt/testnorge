# Inntektsmelding-stub

Microservice for å mappe inntektsmeldinger i JSON format til XML format basert på [kodeverk](https://github.com/navikt/tjenestespesifikasjoner/blob/master/nav-altinn-inntektsmelding/src/main/xsd/).  
Foreløpig er bare 201812 format støttet.


## Mangler
- [ ] Testing
- [ ] Bedre støtte for enkelt å legge til nye endringer i meldingene (?) 
- [x] CircleCI prosjekt er ikke satt opp
- [x] Deploy access er ikke satt opp
- [ ] Oversettelse fra inntektstub v2-format til altinn inntektsmelding

### Implementeres i adapteret testnorge-inntekt
- [ ] Hente data fra inntektstub (Må vente på inntektstub v.2)
- [ ] Hente syntetisk data fra synt-rest
- [x] Foreløpig ikke implementert innsending av meldingene til Joark

## REG-5838
Trenger tre funksjonaliteter til inntektsmelding-stub (mest ryddig med egen adapter)
 - 1a: motta FNR og litt info om arbeidsforhold fra requesten, hent fra inntektstub inntektsdata på fnr og 
       arbeidsforhold, og legg dette i inntektsmeldingen.
       _venter på inntektstub v.2_
 - 1b: hvis ikke finnes, opprett syntetisk og legg i inntektstub, og legg inn i inntektsmeldingen
 - 2: bevisst opprett avvik mellom inntektsmelding og inntektstub (eget endepunkt for å opprette avvik). Hvis det ikke 
      er noe i inntektstub, syntetiser data som avviker fra det som ble sendt inn i requesten.
 - 3: motta json med gitt antall felt, så syntetiserer vi resten og legger i inntektsmeldingen (trenger ikke sende til 
      inntektstub i dette tilfellet)

## Swagger
Swagger finnes under [/api](https://inntektsmelding-stub.nais.preprod.local/api) -endepunktet til applikasjonen.

## Lokal kjøring
Kjør ApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.profiles.active=dev