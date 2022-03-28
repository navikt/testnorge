# Ident-Pool
Ident-pool har oversikt på syntetiske identer, og sjekker mot prod og testmiljøer for eksistens.
Fra ident-pool kan det rekvireres identer basert på født-før og født-etter samt kjønn og type.
Spesifikke identer kan også allokeres.
Identer kan frigjøres og benyttes om igjen
 
## Swagger
Swagger finnes under [/swagger](https://ident-pool.dev.adeo.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring

   
### Utenfor utviklerimage
Ha naisdevice kjørende og kjør IdentPoolApplicationStarter med følgende argumenter:
```
-Dspring.profiles.active=local
-Dspring.cloud.vault.token=[Kopier token fra vault]
```
 
### Utviklerimage 
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```

Det kan være nødvendig å ha disse også:
```
-Dhttp.proxyHost=webproxy-utvikler.nav.no
-Dhttps.proxyHost=webproxy-utvikler.nav.no 
-Dhttp.proxyPort=8088 
-Dhttps.proxyPort=8088 
-Dhttp.nonProxyHosts=localhost|127.0.0.1|10.254.0.1|*.local|*.adeo.no|*.nav.no|*.aetat.no|*.devillo.no|*.oera.no|*.nais.io 
```
    
