---
layout: default
title: Generer Navn Service
parent: Applikasjoner
---

# Generer navn-service
App for å generere fiktive navn. Navnene består av et adjektiv (som ofte brukes som fornavn) og et substantiv (som ofte brukes som etternavn).
Det er vurdert at sammenstillingen av slike navn er forskjellig nok fra ekte navn til at data med disse ikke kan forveksles med skarp data.

## Swagger
Swagger finnes under [/swagger](https://generer-navn-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Kjør ApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[vault-token]
-Dspring.profiles.active=dev
```
