---
layout: default title: Forvalter parent: Organisasjon grand_parent: Applikasjoner
---

# Organisasjon Forvalter

Forvalter som oppretter og deretter deployer organisasjoner basert på innsendte kriterier. Håndterer også status per
orgnr når de sendes videre mot EREG.

## Swagger

Swagger finnes under [/swagger](https://testnav-organisasjon-forvalter.dev.intern.nav.no/swagger) -endepunktet til
applikasjonen.

## Lokal kjøring

Ha naisdevice kjørende og kjør OrganisasjonForvalterApplicationStarter med følgende argumenter:

``` 
-Dspring.profiles.active=dev
-Dspring.cloud.vault.token=[vault-token]
```

og legg til i VM options:

``` 
--add-opens java.base/java.lang=ALL-UNNAMED
``` 
