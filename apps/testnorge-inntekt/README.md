---
layout: default
title: Testnorge Inntekt
parent: Applikasjoner
---

# testnorge-inntekt
Adapter for inntekt

Tilbyr endepunkter for å opprette et gitt antall syntetiske inntekter, AltinnInntektmedlinger.
Arbeidsforholdene til inntektsmeldingene blir validert mot Aareg i miljø.

### Altinn Inntekt
Altinn inntekt gjør kall til [inntektsmelding-stub](https://github.com/navikt/testnorge/tree/master/apps/inntektsmelding-stub), som oversetter inntektsmeldinger basert på 
[kodeverk](https://github.com/navikt/tjenestespesifikasjoner/tree/master/nav-altinn-inntektsmelding/src/main/xsd) og legger dem så inn i Joark.

## Swagger
Swagger finnes under [/api](https://testnorge-inntekt.nais.preprod.local/api) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør ApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=local
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
    