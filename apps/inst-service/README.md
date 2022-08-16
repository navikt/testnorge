# Testnav-inst-service
Testnav-inst-service er en applikasjon for oppretting, henting og sletting av institusjonsforholdsmeldinger i Inst.

## Swagger
Swagger finnes under [/swagger](https://testnav-inst-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør TestnorgeInstApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
 