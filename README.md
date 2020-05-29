![Build](https://github.com/navikt/testnorge/workflows/Build/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_testnorge&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_testnorge)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_testnorge&metric=coverage)](https://sonarcloud.io/dashboard?id=navikt_testnorge)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navikt_testnorge&metric=ncloc)](https://sonarcloud.io/dashboard?id=navikt_testnorge)

# testnorge

## Kjør lokalt

`mvn --settings maven-settings.xml -DNAV_TOKEN=xxx clean install`

NAV_TOKEN må lages i din github konto. (Dette er noe som vi jobber med å fjerne)

## Deploy status

![Deploy testnorge-ereg-mapper](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-ereg-mapper/badge.svg)
![Deploy testnorge-elsam](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-elsam/badge.svg)
![Deploy testnorge-medl](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-medl/badge.svg)
![Deploy testnorge-arena](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-arena/badge.svg)
![Deploy testnorge-skd](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-skd/badge.svg)
![Deploy testnorge-inst](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-inst/badge.svg)
![Deploy testnorge-statisk-data-forvalter](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-statisk-data-forvalter/badge.svg)
![Deploy testnorge-tp](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-tp/badge.svg)
![Deploy testnorge-hodejegeren](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-hodejegeren/badge.svg)
![Deploy testnorge-spion](https://github.com/navikt/testnorge/workflows/Deploy%20testnorge-spion/badge.svg)

## Migrering inn i monorepo

Migrering av andre repoer inn i monorepo.
```
git remote add -f $REPO_NAVN https://github.com/navikt/$REPO_NAVN.git
git merge -s ours --no-commit $REPO_NAVN/master --allow-unrelated-histories
git read-tree --prefix=apps/$REPO_NAVN/ -u $REPO_NAVN/master
git commit -m "Migrering av $REPO_NAVN inn i testnorge"
git push
```
