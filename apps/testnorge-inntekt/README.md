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
   
### Utviklerimage
Kjør ApplicationStarter med følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=local
```

### Utenfor utviklerimage

#### Windows
Ha BIG-IP Edge Client kjørende og kjør ApplicationStarter med samme argumenter som for utviklerimage.
    
#### Mac
Ha Nav-Tunnel kjørende og kjør ApplicationStarter med samme argumenter som for utviklerimage og legg til følgende argumenter:
```
-DsocksProxyHost=127.0.0.1
-DsocksProxyPort=14122
-DsocksNonProxyHosts=127.0.0.1|dl.bintray.com|repo.maven.apache.org|maven.adeo.no|packages.confluent.io|confluent.io|maven.xwiki.org|maven.repository.redhat.com
```
