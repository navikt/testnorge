# testnav-arbeidsforhold-service
API for arbeidsforhold som hentes fra testnav-aareg-proxy.

## Swagger
Swagger finnes under [/swagger](https://testnav-arbeidsforhold-service.intern.dev.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør ArbeidsforholdApiApplicationStarter med følgende argumenter:
```
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```