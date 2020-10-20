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

Legg inn dette i **din** maven settings.xml fil:
```
<settings>
    <profiles>
        <profile>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <repositories>
                <repository>
                    <id>internal-mirror-github-navikt</id>
                    <url>https://repo.adeo.no/repository/github-package-registry-navikt/</url>
                </repository>
            </repositories>
        </profile>
    </profiles>
</settings>
```

Så kjør `mvn clean install`

## Deploy status

![Deploy testnorge-ereg-mapper](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-ereg-mapper/badge.svg)
![Deploy testnorge-medl](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-medl/badge.svg)
![Deploy testnorge-arena](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-arena/badge.svg)
![Deploy testnorge-skd](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-skd/badge.svg)
![Deploy testnorge-inst](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-inst/badge.svg)
![Deploy testnorge-statisk-data-forvalter](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-statisk-data-forvalter/badge.svg)
![Deploy testnorge-tp](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-tp/badge.svg)
![Deploy testnorge-hodejegeren](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-hodejegeren/badge.svg)
![Deploy testnorge-spion](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-spion/badge.svg)
![Deploy testnorge-aareg](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-aareg/badge.svg)
![Deploy testnorge-sigrun](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-sigrun/badge.svg)
![Deploy testnorge-sam](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-sam/badge.svg)
![Deploy orkestratoren](https://github.com/navikt/testnorge/workflows/Deploy%20orkestratoren/badge.svg)
![Deploy inntektsmelding-stub](https://github.com/navikt/testnorge/workflows/Deploy%20inntektsmelding-stub/badge.svg)
![Deploy testnorge-frikort](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-frikort/badge.svg)
![Deploy testnorge-aaregstub](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-aaregstub/badge.svg)
![Deploy testnorge-tss](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-tss/badge.svg)
![Deploy testnorge-nav-endringsmeldinger](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-nav-endringsmeldinger/badge.svg)
![Deploy testnorge-token-provider](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-token-provider/badge.svg)
![Deploy testnorge-inntekt](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-inntekt/badge.svg)
![Deploy helsepersonell-api](https://github.com/navikt/testnorge/workflows/Deploy%20helsepersonell-api/badge.svg)
![Deploy avhengighetsanalyse-frontend](https://github.com/navikt/testnorge/workflows/Deploy%20avhengighetsanalyse-frontend/badge.svg)
![Deploy person-api](https://github.com/navikt/testnorge/workflows/Deploy%20person-api/badge.svg)
![Deploy sykemelding-api](https://github.com/navikt/testnorge/workflows/Deploy%20sykemelding-api/badge.svg)
![Deploy organisasjon-api](https://github.com/navikt/testnorge/workflows/Deploy%20organisasjon-api/badge.svg)
![Deploy arbeidsforhold-api](https://github.com/navikt/testnorge/workflows/Deploy%20arbeidsforhold-api/badge.svg)
![Deploy synt-sykemelding-api](https://github.com/navikt/testnorge/workflows/Deploy%20synt-sykemelding-api/badge.svg)
![Deploy testnorge-populasjoner](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-populasjoner/badge.svg)
![Deploy hendelse-api](https://github.com/navikt/testnorge/workflows/Deploy%20hendelse-api/badge.svg)
![Deploy brreg-stub](https://github.com/navikt/testnorge/workflows/Deploy%20brreg-stub/badge.svg)
![Deploy udi-stub](https://github.com/navikt/testnorge/workflows/Deploy%20udi-stub/badge.svg)
![Deploy synt-person-api](https://github.com/navikt/testnorge/workflows/Deploy%20synt-person-api%20prod/badge.svg)

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
