## Testnorge-Arbeidsforhold-api


### Swagger
Swagger finnes under [/api](https://testnorge-arbeidsforhold-api.nais.preprod.local/api) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør ArbeidsforholdApiApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.profiles.active=dev
 - -DSTS_TOKEN_PROVIDER_USERNAME=[username til token provider]
 - -DSTS_TOKEN_PROVIDER_PASSWORD=[passord til token provider]
 - -DSTS_TOKEN_PROVIDER_URL=[url til token provider]