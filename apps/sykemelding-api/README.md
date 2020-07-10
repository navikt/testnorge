## Testnorge-Sykemelding-api
API for sykemeldinger.

### Swagger
Swagger finnes under [/api](https://testnorge-sykemelding-api.nais.preprod.local/api) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør PersonApiApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.cloud.vault.token=[kopier token fra vault]
 - -Dspring.profiles.active=dev