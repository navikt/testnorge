# brreg-stub
Stub av tjenester NAV bruker mot BRREG. Stubben har også endepunkt for å se, slette og opprette testdata.

## Bruk

Applikasjonen eksponerer utvalgte deler av SOAP-tjenestene til Brønnøysundregisterene mot Enhetsregisteret.

[Dokumentasjon fra Brønnøysundregisterene på tjenesten](https://data.brreg.no/enhetsregisteret/api/docs/index.html)

Det er tjenesten **hentRoller** og **hentRolleutskrift**/Rolleoversikt som eksponeres med egen data.

Data opprettes via REST-tjenesten

For dokumentasjon av applikasjonen sine endepunkter:
 - `/api`
 - `WSDL: /ws`

## Deployment

Applikasjonen deployes til dev-fss ved bruk av [Github Actions.](https://github.com/navikt/testnorge/actions) Nytt bygg trigges ved PR eller merge til master. 

Applikasjonen kan også startes opp lokalt ved å bruke LocalApplicationStarter

## Lokal kjøring
* [Generelt.](../../docs/local_general.md)
* [Lokal PSQL i Docker](../../docs/local_db.md)

