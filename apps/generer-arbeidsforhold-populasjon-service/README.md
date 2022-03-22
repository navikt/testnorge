# Generer-arbeidsforhold-populasjon-service

Service som genererer arbeidsforhold i et valgt miljø for en spesifisert tidsperiode.

## Swagger

Swagger finnes under [/swagger](https://testnav-generer-arbeidsforhold-populasjon-service.dev.intern.nav.no/swagger)
-endepunktet til applikasjonen

## Lokal kjøring

Ha naisdevice kjørende og kjør GenererArbeidsforholdPopulasjonServiceApplicationStarter med følgende argumenter:

```
-Dspring.cloud.vault.token=[Copy token fra Vault]
-Dspring.profiles.active=dev
```

### Utviklerimage

I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:

```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```