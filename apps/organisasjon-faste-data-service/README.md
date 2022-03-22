---
layout: default title: Faste Data Service parent: Organisasjon grand_parent: Applikasjoner
---

# Organisasjon Faste data service

Service som utfører CRUD funksjonalitet for organisasjoner mot EREG. Ofte benyttet etter prod laster mot miljøer for å
sende inn organisasjoner fra grupper som er blitt overskrevet.

## Swagger

Swagger finnes under [/swagger](https://testnav-organisasjon-faste-data-service.dev.intern.nav.no/swagger) -endepunktet
til applikasjonen.

## Lokal kjøring

Ha naisdevice kjørende og kjør OrganisasjonFasteDataServiceApplicationStarter med følgende argumenter:

```
-Dspring.cloud.vault.token=[Copy token fra Vault]
-Dspring.profiles.active=dev
```
