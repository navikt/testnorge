# Adresse-service
Adresse-service gir tilgang til adresser fra PDL.  
Søk kan gjøres på postnummer, kommunenummer, fritekstsøk, mm
 
## Swagger
Swagger finnes under [/api](https://testnav-adresse-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør AdresseServiceApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=local
--add-opens java.base/java.lang=ALL-UNNAMED
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
