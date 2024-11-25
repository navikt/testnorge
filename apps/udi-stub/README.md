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
* [Generelt.](../../docs/local_general.md)
* [Lokal PSQL i Docker](../../docs/local_db.md)

For å kjøre tester og bygge appen lokalt må Docker (Colima kan brukes på Mac) kjøre og man er nødt til å sette disse
miljøvariablene:

```
DOCKER_HOST=unix://${HOME}/.colima/default/docker.sock
TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
TESTCONTAINERS_RYUK_DISABLED=true
```