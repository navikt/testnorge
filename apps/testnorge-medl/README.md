## Testnorge-Medl
Testnorge-Medl er en applikasjon som henter syntetiske medlemskap (medlem i folketrygden) og populerer disse med identer før den legger medlemskapene inn i medl-databasen.

### Lokal kjøring
Kjør LocalApplicationStarter med føgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -DTESTNORGE_IDA_CREDENTIAL_AKTOER_USERNAME=[brukernavn til IDA-bruker (Z-ident)] 
 - -DTESTNORGE_IDA_CREDENTIAL_AKTOER_PASSWORD=[passord til IDA-bruker]
 - -DMEDL_DB_Q2_URL=[database-url]
 - -DMEDL_DB_Q2_USERNAME=[database-brukernavn]
 - -DMEDL_DB_Q2_PASSWORD=[database-passord]