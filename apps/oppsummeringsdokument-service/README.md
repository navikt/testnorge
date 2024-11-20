---
layout: default
title: Service
parent: Oppsummeringsdokument
grand_parent: Applikasjoner
---

# Oppsummeringsdokument

API for å sende inn oppsummeringsdokumenter til AAreg, og søke i de innsendte dokumentene.

Applikasjonen kan nås fra [/swagger](https://testnav-oppsummeringsdokument-service.intern.dev.nav.no/swagger) -endepunktet.

## Lokal kjøring
* [Generelt.](../../docs/local_general.md)

For å kjøre lokalt med OpenSearch:

```
> docker run -p 9200:9200 -p 9600:9600 -e "discovery.type=single-node" -e "plugins.security.disabled=true" --name opensearch-node -d opensearchproject/opensearch:latest
```

Liste over alle indekser:

http://localhost:9200/_cat/indices?v

`curl` for å søke lokalt:

```
> curl -X GET "localhost:9200/oppsummeringsdokument-8/_search?pretty" -H 'Content-Type: application/json' -d'
{
"query": {
"match": {"miljo": "q1"}
}
}
'
```