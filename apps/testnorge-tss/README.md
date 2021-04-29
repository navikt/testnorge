---
layout: default
title: Testnorge TSS
parent: Applikasjoner
---

# testnorge-tss
Testnorge-TSS er en applikasjon som oppretter samhandlere i TSS databasen. 

## Samhandlere

Det er opprettet en flat distribusjon av samhandlere i TSS i Q2, med små variasjoner. De fleste samhandler typer bruker enten ORGnummer eller FNR. 

For samhandlere med ORGnummer er det definert en csv fil som inneholder de forskjellige ORGnummerene pr samhandlertype. 

### NAV samhandlere
NAV samhandlere vil bli hentet ut fra q0. Disse blir ikke opprettet av applikasjonen.

### INST 
INST samhandlere er ikke mulig å opprette via applikasjonen ettersom de krever en institusjons id som ikke enda er støttet.

## Swagger
Swagger finnes under [/api](https://testnorge-tss.dev.intern.nav.no/api) -endepunktet til applikasjonen.

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

