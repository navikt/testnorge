# Organisasjon Forvalter

Forvalter som oppretter og deretter deployer organisasjoner basert på innsendte kriterier. Håndterer også status per
orgnr når de sendes videre mot EREG.

## Swagger

Swagger finnes under [/swagger](https://testnav-organisasjon-forvalter.intern.dev.nav.no/swagger) -endepunktet til
applikasjonen.

## Lokal kjøring

Ha naisdevice kjørende og kjør OrganisasjonForvalterApplicationStarter med følgende argumenter:

``` 
-Dspring.profiles.active=local
-Dspring.cloud.vault.token=[vault-token]
```

og legg til i VM options:

``` 
--add-opens java.base/java.lang=ALL-UNNAMED
``` 

Applikasjonen er avhengig av en lokal PSQL-database. For mer informasjon se [egen dokumentasjon](../../docs/local_db.md).