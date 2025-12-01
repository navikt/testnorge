# testnav-dolly-search-service
Service som forvalter søking på personer basert på innsendte kriterier

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)
* [OpenSearch.](../../docs/modules/ROOT/pages/local/local_opensearch.adoc)

For å nå Aiven OpenSearch-instansen lokalt, kan alternativt følgende kommandoer benyttes for å hente påloggingsinformasjon:

> nais aiven create opensearch ignored dolly -i bestillinger -a read -s \<ownsecretname\> -e 10

> nais aiven get opensearch d\<ownsecretname\> dolly

