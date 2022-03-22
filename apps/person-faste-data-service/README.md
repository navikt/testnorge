---
layout: default title: Person Faste Data Service parent: Applikasjoner
---

# Person Faste Data Service

Service som administrerer de faste data personene som er i bruk i Dolly.

## Swagger

Swagger finnes under [/swagger](https://testnav-person-faste-data-service.dev.intern.nav.no/swagger) -endepunktet til
applikasjonen.

## Lokal kjøring

Ha naisdevice kjørende og kjør PersonFasteDataServiceApplicationStarter med følgende argumenter:

``` 
-Dspring.profiles.active=local
-Dspring.cloud.vault.token=[vault-token]
```
