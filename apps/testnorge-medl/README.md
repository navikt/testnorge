## Testnorge-Medl
Testnorge-Medl er en applikasjon som henter syntetiske medlemskap (medlem i folketrygden) og populerer disse med identer før den legger medlemskapene inn i medl-databasen.

### Lokal kjøring
Kjør LocalApplicationStarter med føgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.cloud.vault.token=[Kopier token fra vault]