# Tenor Search Service
Service som formidler søk til Tenor testdata hos Skatteetaten.

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)
* [OpenSearch.](../../docs/modules/ROOT/pages/local/local_opensearch.adoc)

For å nå Aiven OpenSearch-instansen lokalt, kan alternativt følgende kommandoer benyttes for å hente påloggingsinformasjon:

> nais opensearch credentials bestillinger --team dolly --environment dev --permission READ --ttl 14d 