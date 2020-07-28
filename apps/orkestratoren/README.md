## Orkestratoren
Orkestratoren er applikasjonen som orkestrerer opprettelse av syntetiske hendelser i den syntetiske populasjonen "mininorge".

### Swagger
Swagger finnes under [/api](https://orkestratoren.nais.preprod.local/api) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør LocalApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.cloud.vault.token=[copy token fra vault]