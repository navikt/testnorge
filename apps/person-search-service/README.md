---
layout: default title: Personsøk Service parent: Applikasjoner
---

# person-search-service

Service som forvalter søking på personer basert på innsendte kriterier

## Swagger

Swagger finnes under [/swagger](https://person-search-service.dev.intern.nav.no/swagger)
-endepunktet til applikasjonen.

## Lokal utvikling

Ha naisdevice kjørende og kjør PersonSearchServiceApplicationStarter med følgende argumenter:

```
-Dspring.cloud.vault.token=[Kopi av token fra vault]
-Dspring.profiles.active=dev
```

og kjør opp en lokal version av en elasticsearch instance

```
docker run -d --name es762 -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.10.1
```
