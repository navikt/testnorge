# Miljoer-service
App for å sjekke hvilke miljøer i test og preprod som er tilgjengelige nå.

## Swagger
Swagger finnes under [/swagger](https://miljoer-service.intern.dev.nav.no/swagger) -endepunktet til applikasjonen.

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