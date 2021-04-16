---
layout: default
title: Inntektsmelding Generator Service
parent: Applikasjoner
---


# Inntektsmelding generator

Microservice for å mappe inntektsmeldinger i JSON format til XML format basert på [kodeverk](https://github.com/navikt/tjenestespesifikasjoner/blob/master/nav-altinn-inntektsmelding/src/main/xsd/).  
Foreløpig er bare 201812 format støttet.

## Swagger
Swagger finnes under [/api](https://testnav-inntektsmelding-generator-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Kjør ApplicationStarter med følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
-Dspring.profiles.active=dev
```
