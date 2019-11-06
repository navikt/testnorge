# inntektsmelding-stub

Stub for lagring og innsending av inntektsmeldinger. 

## Mangler
- [ ] Foreløpig ikke implementert innsending av meldingene
- [ ] XMl output feiler, se todo i koden
- [x] Datbase er ikke satt opp i vault
- [x] Migrasjonscript er ikke på plass
- [ ] Testing
- [ ] Bedre støtte for enkelt å legge til nye endringer i meldingene
- [x] CircleCI prosjekt er ikke satt opp
- [x] Deploy access er ikke satt opp

Trenger tre funksjonaliteter til inntektsmelding-stub (mest ryddig med egen adapter)
 - 1a: motta FNR og litt info om arbeidsforhold fra requesten, hent fra inntektstub inntektsdata på fnr og 
       arbeidsforhold, og legg dette i inntektsmeldingen.
       _venter på inntektstub v.2_
 - 1b: hvis ikke finnes, opprett syntetisk og legg i inntektstub, og legg inn i inntektsmeldingen
 - 2: bevisst opprett avvik mellom inntektsmelding og inntektstub (eget endepunkt for å opprette avvik). Hvis det ikke 
      er noe i inntektstub, syntetiser data som avviker fra det som ble sendt inn i requesten.
 - 3: motta json med gitt antall felt, så syntetiserer vi resten og legger i inntektsmeldingen (trenger ikke sende til 
      inntektstub i dette tilfellet)

## XSD
https://github.com/navikt/tjenestespesifikasjoner/blob/master/nav-altinn-inntektsmelding/src/main/xsd/Inntektsmelding20181211.xsd
