![Build](https://github.com/navikt/dolly-backend/workflows/Build/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=coverage)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=ncloc)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)

# dolly-backend

## Avhenighetsanalyse

## Utenfor utviklerimage

https://dolly.ekstern.dev.nav.no/swagger-ui.html

## Fra utviklerimage

https://dolly-backend.dev.intern.nav.no/swagger-ui.html

## Kjør lokalt

https://dolly-backend.dev.intern.nav.no/swagger-ui.html
**NB: `naisdevice` må kjøre og være grønn.**

Så kjør `./gradlew clean build`.

Deretter kan DollyBackendApplicationStarter startes med disse VM options:

`-Dspring.profiles.active=local --add-opens java.base/java.lang=ALL-UNNAMED -Dspring.cloud.vault.token=*TOKEN*`

## Deploy status

![Deploy dev t1](https://github.com/navikt/dolly-backend/workflows/Deploy%20dev%20t1/badge.svg)
![Deploy dev t2](https://github.com/navikt/dolly-backend/workflows/Deploy%20dev%20t2/badge.svg)
![Deploy dev u2](https://github.com/navikt/dolly-backend/workflows/Deploy%20dev%20u2/badge.svg)
![Deploy dev default](https://github.com/navikt/dolly-backend/workflows/Deploy%20dev%20default/badge.svg)
