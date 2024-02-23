---
layout: default
title: Service
parent: Oppsummeringsdokument
grand_parent: Applikasjoner
---

# Oppsummeringsdokument

API for å sende inn oppsummeringsdokumenter til AAreg, og søke i de innsendte dokumentene.

Applikasjonen kan nås fra [/swagger](https://oppsummeringsdokument-service.intern.dev.nav.no/swagger) -endepunktet.

## Lokal utvikling

```
-Dspring.cloud.vault.token={VAULT_TOKEN} -Dspring.profiles.active=dev
```

For å kjøre lokalt med opensearch:

docker run -p 9200:9200 -p 9600:9600 -e "discovery.type=single-node" -e "plugins.security.disabled=true" --name opensearch-node -d opensearchproject/opensearch:latest

lister alle indekser:

http://localhost:9200/_cat/indices?v

curl kommandoer for å søke lokalt:

curl -X GET "localhost:9200/oppsummeringsdokument-8/_search?pretty" -H 'Content-Type: application/json' -d'
{
"query": {
"match": {"miljo": "q1"}
}
}
'