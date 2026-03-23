# Adresse-service
Adresse-service gir tilgang til adresser fra PDL.  
Søk kan gjøres på postnummer, kommunenummer, fritekstsøk, mm

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)

Genenrering av midlertidig passord for opensearch i dolly:
nais aiven create opensearch ignored dolly -i bestillinger -a admin -s dolly-secret  -e 10  