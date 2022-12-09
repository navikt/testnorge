# Batch-bestilling-service
App for å sende inn et stort antall bestillinger mot backend over tid, for å ikke overbelaste Dolly

## Swagger
Swagger finnes under [/swagger](https://testnav-batch-bestilling-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør MiljoerServiceApplicationStarter med følgende argumenter:
```
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```