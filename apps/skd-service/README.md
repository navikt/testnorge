# Testnav-skd-service
Testnav-skd-service er en applikasjon brukt for å starte avspilling av en TPSF-avspillergruppe. Dette innebærer at alle 
skd-meldingene i en gitt gruppe sendes til TPS i et gitt miljø.

## Swagger
Swagger finnes under [/swagger](https://testnav-skd-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring

Start `SkdServiceApplicationStarter` med følgenede props:

```
-Dspring.profiles.active=dev 
-Dspring.cloud.vault.token=<<VAULT_TOKEN>>
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
