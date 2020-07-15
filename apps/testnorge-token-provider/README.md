## Testnorge-Token-Provider

### Swagger
Swagger finnes under [/swagger-ui.html](https://testnorge-token-provider.nais.preprod.local/swagger-ui.html) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør ApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.cloud.vault.token=[Kopier token fra vault]
 - -Dspring.profiles.active=local
