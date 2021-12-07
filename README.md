![Build](https://github.com/navikt/testnorge/workflows/Build/badge.svg)
![Release](https://github.com/navikt/testnorge/workflows/Release/badge.svg)
[![tag](https://img.shields.io/github/v/tag/navikt/testnorge)](https://github.com/navikt/testnorge/releases)

# testnav

Info/lenker til Team Dollys interne verktøy finnes [her](https://navikt.github.io/dolly/).

## Bygging/Kjøring
### Kjør lokalt

**NB: `navtunnel` må kjøre, eller man må være logget inn med `naisdevice`**

For å kunne hente ned alle avheningehter må det opprettes en Personal access tokens fra https://github.com/settings/tokens. Denne token må legges inn i system variabler NAV_TOKEN.

Fra Mac
```
/etc/profile
export NAV_TOKEN=xxxx-yyyy-zzzz
```

Gradle følger med prosjektet og `./gradlew build` vil derfor fungere. `gradle build` bruker lokalt installert Gradle.


### Utviklerimage
- Opprett Personal access tokens i Github og legg til token som systemvariabelen NAV_TOKEN (se forklaring over)
- Opprett `gradle.properties` under `C:/Users/%USERNAME%/.gradle` med innhold (bytt ut truststorepassord og -path):
```
systemProp.http.proxyHost=webproxy-utvikler.nav.no
systemProp.http.proxyPort=8088
systemProp.http.nonProxyHosts=localhost|127.0.0.1|*.local|*.adeo.no|*.nav.no|*.aetat.no|*.devillo.no|*.oera.no|*devel
systemProp.https.proxyHost=webproxy-utvikler.nav.no
systemProp.https.proxyPort=8088
systemProp.https.nonProxyHosts=localhost|127.0.0.1|*.local|*.adeo.no|*.nav.no|*.aetat.no|*.devillo.no|*.oera.no|*devel
systemProp.javax.net.ssl.trustStorePassword=TRUSTSTORE_PASS
systemProp.javax.net.ssl.trustStore=TRUSTSTORE_PATH
```
- Legg til sertifikat til truststore: https://plugins.gradle.org, https://dl.bintray.com/gradle/gradle-plugins og https://repository-cdn.liferay.com/nexus/content/groups/public 
    - Åpne URL i nettleser
    - Trykk på hengelås til venstre for URL og klikk på "Sertifikat"
    - Velg fanen "Detaljer" og klikk "Kopier til fil". Last ned DER-kodet binær. Merk at sertifikatet får filformat .cer
    - Sertifikat legges til TrustStore ved hjelp av kommandoen:
        ``keytool -import -trustcacerts -alias ALIAS -file DIN_DOWNLOAD_DIR/SERTIFIKAT_FILNAVN.cer -keystore PATH_TIL_KEYSTORE/KEYSTORE_FILNAVN.jts``
- Hvis punktene over ikke er tilstrekkelig og prosjektet sliter med å hente pakker fra maven, forsøk å legge til denne 
under repositories i `java-conventions.gradle` (MERK! Denne må fjernes igjen før commit):
``` 
maven {
          url = uri('https://repo.adeo.no/repository/github-package-registry-navikt')
      }
```
## Dokumentasjon
Enhver applikasjon skal ha dokumentasjon i fila `<min-testnorge-app>/README.md`. Hver av disse filene må starte med
```
---
layout: default
title: min-app
parent: Applikasjoner
---

# min-app
[...]
```
for å bli vist på riktig måte under https://navikt.github.io/testnorge.
README-filen skal beskrive kort hva appen er til, og hvordan den fungerer. Det er også fint om du beskriver kort
info om lokal kjøring osv.

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