## Testnorge-Person-api

### Swagger
Swagger finnes under [/api](https://testnorge-person-api.nais.preprod.local/api) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør PersonApiApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.profiles.active=dev
 - -DSTS_TOKEN_PROVIDER_USERNAME=[token provider brukernavn]
 - -DSTS_TOKEN_PROVIDER_PASSWORD=[token provider passord]
 - -DSTS_TOKEN_PROVIDER_URL=[url til token provider]