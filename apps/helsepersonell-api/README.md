## Testnorge-Helsepersonell-api


### Swagger
Swagger finnes under [/api](https://testnorge-helsepersonell-api.nais.preprod.local/api) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør HelsepersonellApiApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.profiles.active=dev
 - -DHODEJEGEREN_URL=[url til hodejegeren]
 - -DSAMHANDLERREGISTERET_URL=[url til samhandlerregisteret]
 - -DCACHE_HOURS_LEGER=[cache hours leger]
 - -DAVSPILLERGRUPPE_LEGER=[avspillergruppe]