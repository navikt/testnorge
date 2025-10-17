# Dolly

Prosjekt for å opprette og konfigurere personer knyttet til fellesregistrene i NAV.

## Dokumentasjon

## Lokal kjøring
* [Generelt.](../../docs/local_general.md)
* [Secret Manager.](../../docs/local_secretmanager.md)

### Javascript

- Følg oppskriften i Java, denne kreves for å kjøre Dolly lokalt
- Kjør applikasjonen med npm start (fra ./src/main/js)

**NB: Legg til i .npmrc filen for å kunne installere pakker fra github packages:**

```
//npm.pkg.github.com/:_authToken=ghp...
```

Github Auth token kan hentes fra Github settings -> Developer settings (helt nederst) -> Personal access tokens ->
generate new token (må ha read:packages og repo tilgang)

.npmrc filen skal ligge i brukermappen (cd ~) din, hvis den ikke finnes der må du opprette den.

### Kjøre Valkey lokalt

Local profile bruker p.t. ikke Valkey for sessions, men dersom det er ønskelig er det mulig å bruke [docker-compose.yml](./docker-compose.yml) for lokal testing.

### Playwright - E2E testing

Playwright brukes til å teste mesteparten av funksjonaliteten i Dolly ved kodeendringer.

Dersom en eller flere av de tre nettleserne som Playwright utfører tester på mangler (Chrome, Firefox og Edge), kjør:

```
sudo npx playwright install msedge
sudo npx playwright install firefox
sudo npx playwright install chrome
```

Deretter kan testene kjøres med kommandoen:

```
npm run test:playwright-run
```

For debugging av testene og utvikling av nye tester brukes:

```
npm run test:playwright-dev
```
