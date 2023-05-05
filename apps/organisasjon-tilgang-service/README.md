## organisajon-tilgang-service

Service som godkjenner tilganger for en spesifisert organisasjoner mot Dolly ved bruk av bankid.

## Swagger

Swagger finnes under [/swagger-ui.html](https://testnav-organisasjon-tilgang-service.intern.dev.nav.no/swagger-ui.html)
-endepunktet til applikasjonen.

## Lokal kjøring

Ha naisdevice kjørende og kjør OrganisasjonTilgangServiceApplicationStarter med følgende argumenter:

``` 
-Dspring.profiles.active=local
-Dspring.cloud.vault.token=[vault-token]
```
