# testnorge-ident-service
App som håndterer service på identer benyttet av Dolly og Orkestrator.

## Swagger
Swagger finnes under [/swagger](https://testnorge-ident-service.dev.adeo.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Kjør ApplicationStarter med følgende argumenter:
 - -Dspring.cloud.vault.token=[vault-token]
 - -Dspring.profiles.active=dev