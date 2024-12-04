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
* [Lokal PostgreSQL.](../../docs/local_db.md)