# Bruker-service

Service som henter og validerer en ansatt fra en reell organisasjon og brukes for autensiering ved bruk av våre
applikasjoner som har bankId innlogging.

## Swagger

Swagger finnes under [/swagger](https://testnav-bruker-service.dev.intern.nav.no/swagger) -endepunktet til
applikasjonen.

## Lokal kjøring

For å kjøre lokalt (BrukerServiceApplicationStarter) må active profile settes til `local`. I tillegg, må cloud vault
token hentes fra Vault. Vault token hentes ved at man logger inn i Vault, trykker på nedtrekksmenyen oppe til høyre, og
trykker på "Copy token".

Disse verdiene fylles deretter inn i VM Options på IDE:

Run -> Edit Configurations -> VM Options

```
-Dspring.cloud.vault.token=(Copy token fra Vault)
-Dspring.profiles.active=local
```

