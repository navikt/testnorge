## Testnorge-Inst
Testnorge-Inst er en applikasjon som henter syntetiske institusjonsforholdsmeldinger og populerer disse med identer før den sender meldingene til Inst.

### Lokal kjøring
Kjør LocalApplicationStarter med føgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -DIDA_USERNAME=[brukernavn til IDA-bruker (Z-ident)] 
 - -DIDA_PASSWORD=[passord til IDA-bruker]

*IDA-identen som brukes må ha rollene:* 
 - 0000-GA-INST_Skriv	
 - 0000-GA-INST_Les