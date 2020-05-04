# Dolly
Prosjekt for å opprette og konfigurere testpersoner knyttet til fellesregistrene i NAV

## Dokumentasjon
### Swagger
Swagger finnes under /api-endepunktet til applikasjonen.

## Lokal kjøring

####Javascript
- Logg på [dolly-t2](https://dolly-t2.nais.preprod.local/) for å få oppdatert OIDC token i nettleser.
- Kjør applikasjonen med npm start (fra C:\path\dolly-web\dolly-web-core\src\main\web_src)

#### Java
For å kjøre lokalt (LocalAppStarter) må active profile settes til "local". I tillegg, for å kunne gjøre kall mot NAIS apper må nav 
truststore settes opp og cloud vault token må hentes fra Vault. Vault token hentes ved at man logger inn i Vault, 
trykker på nedtrekksmenyen oppe til høyre, og trykker på "Copy token".

Hvis du også kjører Dolly-backend lokalt og vil teste Dolly mot den lokale backend versjonen så må `dolly.url` i application-local.yml 
settes til url-en for den lokale versjonen av dolly-backend (eks: `http://localhost:8080`)

__I Intellij:__ 

Run -> Edit Configurations -> VM Options 

Fyll inn verdiene:
* -Djavax.net.ssl.trustStore=C:\path\to\truststore
* -Djavax.net.ssl.trustStorePassword=(Passord)
* -Dspring.cloud.vault.token=(Copy token fra Vault)
* -Dspring.profiles.active=local

####Begge
Hvis du har gjort endringer lokalt på dolly sin frontend-backend (f.eks: lagt til en ny proxy i ProxyController) og 
vil raskt teste hvordan det fungerer sammen med frontend elementer så kan du gjøre følgende:
- Kjør LocalAppStarter (som forklart under Java)
- Kjør applikasjonen med npm run local (fra C:\path\dolly-web\dolly-web-core\src\main\web_src)