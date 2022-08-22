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

https://dolly-backend.dev.intern.nav.no/swagger-ui.html

## Kjør lokalt

https://dolly-backend.dev.intern.nav.no/swagger-ui.html
**NB: `naisdevice` må kjøre og være grønn.**

Så kjør `./gradlew clean build`

Deretter kan DollyBackendApplicationStarter startes med disse VM options:

`-Dspring.profiles.active=local --add-opens java.base/java.lang=ALL-UNNAMED -Dspring.cloud.vault.token=*TOKEN*`