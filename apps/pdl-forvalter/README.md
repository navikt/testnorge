# Testnav-PDL-forvalter
PDL-forvalter bygger og vedlikeholder testpersoner for PDL.

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)
* [Database i GCP](../../docs/modules/ROOT/pages/local/gcp_db.adoc)
* [OpenSearch.](../../docs/modules/ROOT/pages/local/local_opensearch.adoc)

For å nå Aiven OpenSearch-instansen lokalt, kan alternativt følgende kommandoer benyttes for å hente påloggingsinformasjon:

> nais opensearch credentials bestillinger --team dolly --environment dev --permission READ --ttl 14d 