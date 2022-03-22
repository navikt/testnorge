---
layout: default title: Faste data frontend parent: Applikasjoner
---

# Faste data frontend

Applikasjon for å finne faste data som finnes i testmiljøene. Den gir også en oversikt over om de faktiske dataene og
dataene som er lagret i de forskjellige mijøene stemmer overens.

## Ingress

Appen har ikke noen swagger, men søkene kan utføres på [Faste data](https://faste-data-frontend.dev.intern.nav.no)

## Lokal kjøring

Ha naisdevice kjørende og kjør FasteDataFrontendApplicationStarter med følgende argumenter:

```
-Dspring.cloud.vault.token=[Copy token fra Vault]
-Dspring.profiles.active=dev
```