## organisajon-tilgang-frontend

App som forvalter tilgangsstyring for organisasjoner som har tilgang til Dolly gjennom bankid og legger på sikkerhet.

## Ingress

Tjenesten kan nås på [Organisasjon tilgang](https://testnav-organisasjon-tilgang.intern.nav.no)

## Lokal kjøring

Ha naisdevice kjørende og kjør OrganisasjonTilgangFrontendApplicationStarter med følgende argumenter:

``` 
-Dspring.profiles.active=dev
-Dspring.cloud.vault.token=[vault-token]
```
