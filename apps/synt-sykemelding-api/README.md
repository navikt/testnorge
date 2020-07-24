## Testnorge-Synt-Sykemelding-api
API for syntetisering av sykemeldinger

### Swagger
Swagger finnes under [/api](https://testnorge-synt-sykemelding-api.nais.preprod.local/api) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør SyntSykemeldingApiApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.profiles.active=dev