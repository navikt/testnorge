![Build](https://github.com/navikt/testnorge/workflows/Build/badge.svg)
![Release](https://github.com/navikt/testnorge/workflows/Release/badge.svg)
[![tag](https://img.shields.io/github/v/tag/navikt/testnorge)](https://github.com/navikt/testnorge/releases)

# testnav

Info/lenker til Team Dollys interne verktøy finnes [her](https://navikt.github.io/testnorge/).

## Bygging/Kjøring

> **Mac:**
>
> For å kjøre tester som bruker Testcontainers eller kjøre en applikasjon lokalt som krever en tjeneste kjørende i
> Docker, så må disse miljøvariablene settes:
>
> `DOCKER_HOST=unix://${HOME}/.colima/default/docker.sock`\
> `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock`\
> `TESTCONTAINERS_RYUK_DISABLED=true`

Repoet har én Gradle-wrapper, i rota. Kjør den fra modulmappa, for eksempel `cd apps/dolly-backend && ../../gradlew build`.
Modulene har bare `gradle/wrapper/gradle-wrapper.properties`, slik at de kan åpnes som egne prosjekter i IntelliJ.
`./gradlewUpdate.sh` oppdaterer wrapperen og kopierer versjonen ut til alle modulene.

Alle apper, proxyer og mocker bygger image fra `Dockerfile` i rota, med modulmappa som byggekontekst:
`docker build -f Dockerfile apps/dolly-backend`. JVM-flagg for én app står i fila `java-options` i appmappa.
CI sender dem inn som build-arg. Lokalt: `docker build -f Dockerfile --build-arg JAVA_OPTIONS="$(cat apps/dolly-backend/java-options)" apps/dolly-backend`.

### Lokal kjøring

Se `README.md` for hver enkelt applikasjon/proxy. Felles dokumentasjon ligger i [/docs](./docs).

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

## Virtuelt miljø

Kjør kommandoen:

```aiexclude
> JWK=$(cat ./mocks/jwk.json) docker compose up --build
```

Evt. i PowerShell:

```aiexclude
> $env:JWK=(Get-Content -Path ./mocks/jwk.json -Raw) ; docker compose up --build
```

Deretter kan integrasjonstester kjøres fra appens mappe med kommandoen:

```
> ../../gradlew iTest
```

NB: Dette vil kun fungere hvis appen støtter integrasjonstester.

## Kode generert av GitHub Copilot

Dette repoet bruker GitHub Copilot til å generere kode.

Spesifikke repo instruksjoner for copilot ligger i `.github/copilot-instructions.md`
