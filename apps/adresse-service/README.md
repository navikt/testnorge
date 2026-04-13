# Adresse-service
Adresse-service gir tilgang til adresser fra PDL.  
Søk kan gjøres på postnummer, kommunenummer, fritekstsøk, mm

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)

Generering av midlertidig passord for opensearch i dolly:
nais opensearch credentials bestillinger --team dolly --environment dev --permission ADMIN --ttl 10d
