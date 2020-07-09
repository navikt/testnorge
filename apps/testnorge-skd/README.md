## Testnorge-Skd
Testnorge-skd er en applikasjon som henter syntetiske skd-meldinger og fyller disse med passende identer. Disse meldingene legges inn i en avspillergruppe i TPSF, før de "spilles av" og sendes til TPS.

Skd-meldingene har ulike typer, og forårsaker forskjellige endringer på personer i TPS. Det er slik mininorge berikes med nye identer og endringer på eksisterende identer.

### Lokal kjøring
Kjør LocalApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.cloud.vault.token=[Kopier token fra vault]