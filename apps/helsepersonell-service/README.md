# Helsepersonell-service
API for helsepersonell. Finner helsepersonell via Dolly og Samhandlerregisteret. 

Originalt så lå all helsepersonell i en gruppe i TPS-forvalteren (avspillergruppeId 100001163), men har nå blitt 
importert til Dolly gruppe i stedet.

## Swagger
Swagger finnes under [/swagger](https://testnav-helsepersonell-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør HelsepersonellApiApplicationStarter med følgende argumenter:
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
