## Testnorge-Helsepersonell-api
API for helsepersonell.

### Swagger
Swagger finnes under [/api](https://testnorge-helsepersonell-api.nais.preprod.local/api) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør HelsepersonellApiApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.cloud.vault.token=[Copy token fra Vault]
 - -Dspring.profiles.active=dev