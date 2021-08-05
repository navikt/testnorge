---
layout: default
title: Testnorge AAreg
parent: Applikasjoner
---

# Testnorge-Aareg

Testnorge-aareg er en applikasjon som håndterer arbeidsforhold 
ved å motta disse fra synt-pakke eller Dolly, og så sende disse inn i aareg.

Applikasjonen er også koblet til Testnorge-aaregstub,
og sender syntetiserte arbeidsforhold hit for lagring, 
som tilgjengeliggjør enkelt uttrekk av tidligere opprettede syntetiske meldinger.
Meldinger opprettet i Dolly sendes IKKE til denne stubben.

## Branches
Testnorge-Aareg har en master-branch som kjører i Q2, 
og en tilleggsbranch som kjører i U2. 
Denne tilleggsbranchen er tilstede for å gjøre testing med Dolly i U2. 
Det er viktig at begge branchene oppdateres ved videreutvikling av applikasjonen, 
så lenge Dolly har en U2-instans.

## Lokal kjøring
Ha naisdevice kjørende og kjør LocalApplicationStarter med følgende argumenter:
``` 
-Dspring.cloud.vault.token=[vault-token]
```

### Utviklerimage
For øyeblikket er det problemer med å få kjørt testnorge-aareg lokalt i utviklerimage, 
da man ikke får tak i alle dependencies. 
