## Testnorge-Inst
Testnorge-Inst er en applikasjon som henter syntetiske institusjonsforholdsmeldinger og populerer disse med identer før den sender meldingene til Inst.

### Lokal kjøring
Kjør LocalApplicationStarter med føgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dspring.cloud.vault.token=[Kopier token fra vault]
