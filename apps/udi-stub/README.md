---
layout: default
title: UDI Stub
parent: Applikasjoner
---

# UDI-stub

Stub av UDI sine tjenester som brukes av NAV. Stubben inneholder også REST endepunkter for å legge til personer i
databasen.

## Bruk

Applikasjonen eksponerer SOAP tjenesten 'person status' fra UDI, men med egne data.

Data opprettes via REST-tjenesten

For dokumentasjon av applikasjonen sine endepunkter:

- `/swagger-ui.html`
- `WSDL: /ws/udistub.wsdl

## Lokal kjøring

Ha naisdevice kjørende og kjør UdiStubApplicationStarter med følgende argumenter:

```
--add-opens java.base/java.lang=ALL-UNNAMED
-Dspring.profiles.active=local
-Dspring.cloud.vault.token=[vault-token]
```

For å kjøre tester og bygge appen lokalt må Docker (Colima kan brukes på Mac) kjøre og man er nødt til å sette disse
miljøvariablene:

```
DOCKER_HOST=unix://${HOME}/.colima/default/docker.sock
TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
TESTCONTAINERS_RYUK_DISABLED=true
```

## Deployment

Applikasjonen kan kjøre lokalt med en h2 database som kjører i minnet. Applikasjonen kan også kjøres i en skytjeneste
med integrasjon mot Vault og postgresql. Hvis andre integrasjoner er ønskelig må disse implementeres i en egen submodul
hvor integrasjonen defineres f.eks via en @Configuration annotert klasse.

Applikasjonen er avhengig av en lokal PSQL-database. For mer informasjon se [egen dokumentasjon](../../docs/local_db.md).