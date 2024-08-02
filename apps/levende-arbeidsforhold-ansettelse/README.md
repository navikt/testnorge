# Levende arbeidsforhold-service

Fyll inn:

## Swagger

Swagger finnes under [/swagger](https://levende-arbeidsforhold-service.intern.dev.nav.no/swagger) -endepunktet til
applikasjonen.

### Apper testnav-levende-arbeidsforhold-ansettelse prater med 
* pdl-proxy
* aareg-proxy
* tenor-search-service
* kodeverk-service
## Lokal kjøring

Ha naisdevice kjørende og kjør GenererNavnServiceApplicationStarter med følgende argumenter:

```
-Dspring.cloud.vault.token=[vault-token]
-Dspring.profiles.active=dev
```

### Utviklerimage

I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:

```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
