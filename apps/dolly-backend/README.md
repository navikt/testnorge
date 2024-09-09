![Build](https://github.com/navikt/dolly-backend/workflows/Build/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=coverage)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=ncloc)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)

# Dolly-backend

Backend for Dolly som behandler bestillinger og requests fra frontenden og sender disse videre inn i de diverse
systemene hvor testidenter skal ha tilegnet informasjon.

Applikasjonen legger også ved potensielle standard verdier som kreves i API vi er knyttet mot, men som ikke brukerene
trenger å ha noe forhold til under utfylling av bestilling. Noe data blir persistert i postgres db, som f.eks brukerne
av Dolly, bestillingskriterier, hvem som har sendt de inn og status på disse.

## Utenfor utviklerimage

https://dolly.ekstern.dev.nav.no/swagger-ui.html

## Fra utviklerimage

https://dolly-backend.intern.dev.nav.no/swagger-ui.html

## Kjør lokalt

https://dolly-backend.intern.dev.nav.no/swagger-ui.html
**NB: `naisdevice` må kjøre og være grønn.**

Så kjør `./gradlew clean build`

Deretter kan DollyBackendApplicationStarter startes med disse VM options:

`-Dspring.profiles.active=local --add-opens java.base/java.lang=ALL-UNNAMED -Dspring.cloud.vault.token=*TOKEN*`

For å kjøre tester og bygge appen lokalt må Docker (Colima kan brukes på Mac) kjøre og man er nødt til å sette disse
miljøvariablene:

```
DOCKER_HOST=unix://${HOME}/.colima/default/docker.sock
TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
TESTCONTAINERS_RYUK_DISABLED=true
```

For å kjøre lokalt med elastic search:

```
docker run -p 9200:9200 -p 9600:9600 -e "discovery.type=single-node" -e "plugins.security.disabled=true" -e "OPENSEARCH_INITIAL_ADMIN_PASSWORD=YLAgOm}rz#o6#Aq" --name opensearch -d opensearchproject/opensearch:latest
```
Legg merke til passord `YLAgOm}rz#o6#Aq` (tilfeldig [generert](https://www.strongpasswordgenerator.org/), men må være "sterkt" ellers vil ikke OpenSearch starte). 

Applikasjonen er avhengig av en lokal PSQL-database. For mer informasjon se [egen dokumentasjon](../../docs/local_db.md).