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
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Lokal PostgreSQL.](../../docs/modules/ROOT/pages/local/local_db.adoc)