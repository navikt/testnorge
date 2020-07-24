## Orkestratoren
Orkestratoren er applikasjonen som orkestrerer opprettelse av syntetiske hendelser i den syntetiske populasjonen "mininorge".

### Swagger
Swagger finnes under [/api](https://orkestratoren.nais.preprod.local/api) -endepunktet til applikasjonen.

### Lokal kjøring
Kjør LocalApplicationStarter med følgende argumenter:
 - -Djavax.net.ssl.trustStore=[path til lokal truststore]
 - -Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
 - -Dtestnorges.ida.credential.tpsf.username=[brukernavn til IDA-bruker (Z-ident)] 
 - -Dtestnorges.ida.credential.tpsf.password=[passord til IDA-bruker]
 - -DIBM_MQ_CONN_NAME=[MQ conn-name]
 - -DIBM_MQ_CHANNEL=[MQ channel]
 - -DIBM_MQ_QUEUE_MANAGER=[MQ kø manager]
 - -DQUEUE_NAME=[MQ kø navn]
 - -DIBM_MQ_USER=[MQ brukernavn]
 - -DIBM_MQ_PASSWORD=[MQ passord]