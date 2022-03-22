# Amelding-service
Service for å sende syntetiske arbeidsmeldinger videre til oppsummerings-dokument-service.

## Swagger
Swagger finnes under [/swagger](https://testnav-amelding-service.dev.intern.nav.no/swagger) -endepunktet til
applikasjonen.

## Lokal kjøring
For å kjøre lokalt (AMeldingServiceApplicationStarter) må active profile settes til `dev`. I tillegg, må cloud vault token
hentes fra Vault. Vault token hentes ved at man logger inn i Vault, trykker på nedtrekksmenyen oppe til høyre, og
trykker på "Copy token".

Disse verdiene fylles deretter inn i VM Options på IDE:

Run -> Edit Configurations -> VM Options

```
-Dspring.cloud.vault.token=(Copy token fra Vault)
-Dspring.profiles.active=dev
```