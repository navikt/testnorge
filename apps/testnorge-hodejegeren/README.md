---
layout: default
title: Hodejegeren
parent: Applikasjoner
---

# Testnorge-Hodejegeren
Testnorge-Hodejegeren er en applikasjon som henter identer fra avspillergrupper i TPSF. Den har mulighet til å filtrere identene på en rekke ulike egenskaper, og kan også returnere detaljert informasjon om identene, hentet fra TPS.

Applikasjonen tilbyr også en søkefunksjon, der man kan søke etter visse egenskaper, og få identene som tilfredsstiler egenskapene i retur. Dette er mulig fordi applikasjonen har sin egen database som lagrer informasjon om identene, noe som muligjør indeksering av egenskaper.

## Swagger
Swagger finnes under [/api](https://testnorge-hodejegeren.nais.preprod.local/api) -endepunktet til applikasjonen.
 
## Lokal kjøring
Ha naisdevice kjørende og kjør LocalApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[kopier token fra vault]
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må i tillegg ha følgende argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```

**Notat:**  
MongoDB nås ikke utenfor utviklerimage.
