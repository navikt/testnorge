![Build](https://github.com/navikt/testnorge/workflows/Build/badge.svg)
![Release](https://github.com/navikt/testnorge/workflows/Release/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_testnorge&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_testnorge)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_testnorge&metric=coverage)](https://sonarcloud.io/dashboard?id=navikt_testnorge)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navikt_testnorge&metric=ncloc)](https://sonarcloud.io/dashboard?id=navikt_testnorge)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=navikt_testnorge&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=navikt_testnorge)
[![tag](https://img.shields.io/github/v/tag/navikt/testnorge)](https://github.com/navikt/testnorge/releases)

# testnorge

## Avhenighetsanalyse

**Kan kun brukes fra utviklerimage**
https://testnorge-avhengighetsanalyse-frontend.dev.adeo.no/

## Kjør lokalt

**NB: `navtunnel` må kjøre, eller man må være logget inn med `naisdevice`**

For å kunne hente ned alle avheningehter må det opprettes en Personal access tokens fra https://github.com/settings/tokens. Denne token må legges inn i system variabler NAV_TOKEN.

Fra Mac
```
/etc/profile
export NAV_TOKEN=xxxx-yyyy-zzzz
```

Så kjør `gradle build`

## Dokumentasjon
Enhver testnorge-applikasjon skal ha dokumentasjon i fila `<min-testnorge-app>/docs/Implementasjon.md`. Hver av disse filene må starte med
```
---
layout: default
title: min-testnorge-app
parent: Applikasjoner
---

# min-testnorge-app
[...]
```
for å bli vist på riktig måte under https://navikt.github.io/testnorge.
Implementasjonsfilen skal beskrive hvordan appen fungerer, sett i samspill med prosjektet som helhet.  

Info om lokal kjøring osv. skal gis i appens `README.md` fil.

## Migrering inn i monorepo

Migrering av andre repoer inn i monorepo.
```
git remote add -f $REPO_NAVN https://github.com/navikt/$REPO_NAVN.git
git merge -s ours --no-commit $REPO_NAVN/master --allow-unrelated-histories
git read-tree --prefix=apps/$REPO_NAVN/ -u $REPO_NAVN/master
git commit -m "Migrering av $REPO_NAVN inn i testnorge"
git push
```

Eller kjør:
```
/bin/bash  ./.tools/migrate.sh $REPO_NAVN
```
