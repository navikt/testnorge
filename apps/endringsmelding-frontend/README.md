# Endringsmelding-frontend

Frontend for endringsmeldinger. For å bruke denne appen må brukeren være logget inn med en bruker registrert på
azure-applikasjonen `dev-gcp:dolly:endringsmelding-frontend`.

https://endringsmelding.dev.intern.nav.no

## Lokal kjøring

For å kjøre lokalt (DollyFrontendRedirectApplicationStarter) må active profile settes til `dev`. I tillegg, må cloud
vault token hentes fra Vault. Vault token hentes ved at man logger inn i Vault, trykker på nedtrekksmenyen oppe til
høyre, og trykker på "Copy token".

Disse verdiene fylles deretter inn i VM Options på IDE:

Run -> Edit Configurations -> VM Options

```
-Dspring.cloud.vault.token=(Copy token fra Vault)
-Dspring.profiles.active=dev
```
