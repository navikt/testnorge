# Testnav-inntektsmelding-service

Service for å opprette og validere inntektsmeldinger som sendes inn på testpersoner.

## Swagger

Swagger finnes under [/swagger](https://testnav-inntektsmelding-service.dev.intern.nav.no/swagger) -endepunktet til
applikasjonen.

## Lokal kjøring

Ha naisdevice kjørende og kjør InntektsmeldingApplicationStarter med følgende argumenter:

```
-Dspring.cloud.vault.token=[Copy token fra Vault]
-Dspring.profiles.active=dev
```
