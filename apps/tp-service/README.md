# Testnav TP Service
Testnav TP (Tjeneste Pensjon) service er integrasjonen mellom Orkestratoren og TJPEN databasen. Testnav-TP-service går mot TJPEN i gitte miljøer.
 
## Swagger
Swagger finnes under [/swagger](https://testnav-tp-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.
 
## Lokal kjøring
Ha naisdevice kjørende og kjør TpServiceApplicationStarter med følgende argumenter:
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