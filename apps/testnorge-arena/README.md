## Testnorge-Arena

Testnorge-Arena applikasjonen som henter syntetiske vedtak, og velger ut identer som skal inn i disse vedtakene, før de sendes til Arena.

Applikasjonen har også støtte for å opprette syntetiske historiske vedtak, gjennom vedtakshistorikkendepunktet.

### Lokal kjøring
Kjør LocalApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.cloud.vault.token=[Kopier token fra vault]
