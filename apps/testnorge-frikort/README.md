# testnorge-frikort
Testnorge-frikort tilbyr endepunkt for å lage syntetiske egenandelsmeldinger.

## Dokumentasjon

### Swagger
Swagger finnes under [/api](https://testnorge-frikort.nais.preprod.local/api) -endepunktet til applikasjonen.

## Lokal kjøring

### Java
For å kjøre lokalt må active profile settes til "dev". I tillegg, for å kunne gjøre kall mot NAIS apper må nav truststore settes opp 
og cloud vault token må hentes fra Vault. 

__I Intellij:__ 

Run -> Edit Configurations -> VM Options 

Fyll inn verdiene:
* -Djavax.net.ssl.trustStore=C:\path\to\truststore
* -Djavax.net.ssl.trustStorePassword=(Passord)
* -Dspring.cloud.vault.token=(Copy token fra Vault)
* -Dspring.profiles.active=dev