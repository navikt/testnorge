![Build](https://github.com/navikt/testnorge/workflows/Build/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_testnorge&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_testnorge)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_testnorge&metric=coverage)](https://sonarcloud.io/dashboard?id=navikt_testnorge)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navikt_testnorge&metric=ncloc)](https://sonarcloud.io/dashboard?id=navikt_testnorge)

# testnorge

## Avhenighetsanalyse

https://testnorge-avhengighetsanalyse-frontend.nais.preprod.local/

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
/bin/bash  ./tools/migrate.sh $REPO_NAVN
```

