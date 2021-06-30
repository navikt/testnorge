---
layout: default
title: Service
parent: Oppsummeringsdokument
grand_parent: Applikasjoner
---

# Oppsummeringsdokument

API for å sende inn oppsummeringsdokumenter til AAreg, og søke i de innsendte dokumentene.

Applikasjonen kan nås fra [/swagger](https://oppsummeringsdokument-service.dev.intern.nav.no/swagger) -endepunktet.

## Lokal utvikling

```
-Dspring.cloud.vault.token={VAULT_TOKEN} -Dspring.profiles.active=dev -DELASTIC_USERNAME={USERNMAE} -DELASTIC_PASSWORD={PASSWORD} -DELASTIC_HOST={HOST} -DELASTIC_PORT={PORT}
Se teamdolly-elastic secret i kubernetes
```
