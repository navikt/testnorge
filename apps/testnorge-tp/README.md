## Testnorge-TP
Testnorge-TP er integrasjonen mellom Orkestratoren og TJPEN databasen. Testnorge-TP går mot TJPEN i gitte miljøer.

### Lokal kjøring
Kjør LocalApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.cloud.vault.token=[Kopier token fra vault]