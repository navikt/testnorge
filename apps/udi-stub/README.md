---
layout: default
title: UDI Stub
parent: Applikasjoner
---

# UDI-stub

Stub av UDI sine tjenester som brukes av NAV. Stubben inneholder også REST endepunkter for å legge til personer i databasen. 

## Bruk

Applikasjonen eksponerer SOAP tjenesten 'person status' fra UDI, men med egne data.

Data opprettes via REST-tjenesten

For dokumentasjon av applikasjonen sine endepunkter: 
- `/swagger-ui.html`
- `WSDL: /ws`

## Deployment

Applikasjonen kan kjøre lokalt med en h2 database som kjører i minnet. Applikasjonen kan også kjøres i en skytjeneste med integrasjon mot Vault og postgresql. Hvis andre integrasjoner er ønskelig må disse implementeres i en egen submodul hvor integrasjonen defineres f.eks via en @Configuration annotert klasse.
