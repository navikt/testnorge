## Testnorge-Arbeidsforhold-api
API for arbeidsforhold.

### Swagger
Swagger finnes under [/api](https://testnorge-arbeidsforhold-api.nais.preprod.local/api) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør ArbeidsforholdApiApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.cloud.vault.token=[Kopier token fra vault]
 - -Dspring.profiles.active=dev