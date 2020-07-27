## Testnorge-Nav-Endringsmeldinger
Testnorge-Nav-Endringsmeldinger er adapteren for opprettelse av nav-endringsmeldinger som sendes til TPS som XML-meldinger gjennom TPSF.

### Swagger
Swagger finnes under [/api](https://testnorge-nav-endringsmeldinger.nais.preprod.local/api) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør LocalApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.cloud.vault.token=[Kopier token fra vault]