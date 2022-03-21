---
layout: default title: dolly-frontend-redirect parent: Applikasjoner
---

# Dolly-frontend-redirect

App som redirecter fra de utdaterte dolly ingressene til de nye støttede versjonene.

## Lokal kjøring

For å kjøre lokalt (DollyFrontendRedirectApplicationStarter) må active profile settes til `dev`. I tillegg, må cloud
vault token hentes fra Vault. Vault token hentes ved at man logger inn i Vault, trykker på nedtrekksmenyen oppe til
høyre, og trykker på "Copy token".

Disse verdiene fylles deretter inn i VM Options på IDE:

Run -> Edit Configurations -> VM Options

```
-Dspring.cloud.vault.token=(Copy token fra Vault)
-Dspring.profiles.active=dev
```

