# testnorge-inntekt
Adapter for inntekt

Tilbyr endepunkter for å opprette et gitt antall syntetiske inntekter, AltinnInntektmedlinger.
Arbeidsforholdene til inntektsmeldingene blir validert mot Aareg i miljø.

## Dokumentasjon
### Swagger
Swagger finnes under [/api](https://testnorge-inntekt.nais.preprod.local/api) -endepunktet til applikasjonen.

## Lokal kjøring

### Java
For å kjøre lokalt må active profile settes til "local". I tillegg, for å kunne gjøre kall mot NAIS apper må nav truststore settes opp 
og cloud vault token må hentes fra Vault. 

__I Intellij:__ 

Kjør ApplicationStarter med følgende argumenter:
(Run -> Edit Configurations -> VM Options) 
* -Djavax.net.ssl.trustStore=C:\path\to\truststore
* -Djavax.net.ssl.trustStorePassword=(Passord)
* -Dspring.cloud.vault.token=(Copy token fra Vault)
* -Dspring.profiles.active=local