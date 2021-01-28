# Oppsummeringsdokument

## Lokal utvikling

Kjør opp en lokal version av en elasticsearch instance
```
docker run -d --name es762 -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.10.1
```