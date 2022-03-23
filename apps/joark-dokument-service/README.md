# testnav-joark-dokument-service

App for å hente ut joark dokumenter.

## Swagger

Swagger finnes under [/swagger](https://testnav-joark-dokument-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring

Ha naisdevice kjørende og kjør JoarkDokuemntServiceApplicationStarter med følgende argumenter:

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