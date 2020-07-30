# testnorge-spion
Testnorge-spion tilbyr endepunkt for å lage syntetiske vedtak og legge dem på Kafka kø til SPION.

## Dokumentasjon

### Swagger
Swagger finnes under [/api](https://testnorge-spion.nais.preprod.local/api) -endepunktet til applikasjonen.

## Lokal kjøring
Kjør ApplicationStarter med følgende argumenter:
- -Djavax.net.ssl.trustStore=[path til lokal truststore]
- -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
- -Dspring.profiles.active=dev
- -Davspillergruppe.id=[avspillergruppe id]
- -Davspillergruppe.miljoe=[avspillergruppe miljø]
- -Dkafka.brokers.url=[url for kafka brokers]
- -Dkafka.schema.registry.url=[url for kafka schema registry]
- -Dserviceuser.spion.password=[passord til servicebruker]
- -Dserviceuser.spion.username=[brukernavn til servicebruker]
