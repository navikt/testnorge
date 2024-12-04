---
layout: default
title: Service
parent: Oppsummeringsdokument
grand_parent: Applikasjoner
---

# Oppsummeringsdokument
API for å sende inn oppsummeringsdokumenter til AAreg, og søke i de innsendte dokumentene.

## Lokal kjøring
* [Generelt.](../../docs/local_general.md)
* [OpenSearch.](../../docs/local_opensearch.md)

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