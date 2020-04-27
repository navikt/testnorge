# brreg-stub
Stub av tjenester NAV bruker mot BRREG. Stubben har også endepunkt for å se, slette og opprette testdata.

## Bruk

Applikasjonen eksponerer utvalgte deler av SOAP-tjenestene til Brønnøysundregisterene mot Enhetsregisteret.

[Dokumentasjon fra Brønnøysundregisterene på tjenesten](https://www.brreg.no/produkter-og-tjenester/bestille-produkter/maskinlesbare-data-enhetsregisteret/full-tilgang-enhetsregisteret/teknisk-dokumentasjon-for-maskinell-tilgang-til-enhetsregisteret/)

Det er tjenesten **hentRoller** og **hentRolleutskrift**/Rolleoversikt som eksponeres med egen data.

Data opprettes via REST-tjenesten

For dokumentasjon av applikasjonen sine endepunkter: `/api`

`WSDL: /ws`

## Deployment

Applikasjonen deployes til dev-fss ved bruk av [Github Actions.](https://www.brreg.no/produkter-og-tjenester/bestille-produkter/maskinlesbare-data-enhetsregisteret/full-tilgang-enhetsregisteret/teknisk-dokumentasjon-for-maskinell-tilgang-til-enhetsregisteret/
) 

