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

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Lokal PostgreSQL.](../../docs/modules/ROOT/pages/local/local_db.adoc)

