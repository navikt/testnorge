# Dolly

Prosjekt for å opprette og konfigurere personer knyttet til fellesregistrene i NAV.

## Dokumentasjon

## Lokal kjøring
* [Generelt.](../../docs/local_general.md)
* [Secret Manager.](../../docs/local_secretmanager.md)

## Valkey
For å slette en Valkey-instans må den patches. En sletting kan typisk skje ved deploy, ved at man f.eks. endrer navnet til eller fjerner en Valkey-instans. Instans-navnene er på formen `valkey-<namespace>-<instance>`, se [NAIS Console](https://console.nav.cloud.nais.io/team/dolly/valkey). Eksempel for `valkey-dolly-idporten`:
```
kubectl patch valkey valkey-dolly-idporten --type json -p='[{"op": "replace", "path": "/spec/terminationProtection", "value": false}]'
```

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

### Kjøre Redis lokalt

Evt last ned colima og kjør

```
colima start
```

For å kjøre Redis-instans lokalt må du ha Docker kjørende og kjør kommandoen:

```
docker run --name redis-image -d -p 6379:6379 redis:6.2.6
```

Deretter må du fjerne "local" fra profiles i LocalConfig og legge til "local" i profiles for ProdConfig (husk å endre
dette tilbake
før noe pushes til master). Etter dette kan du kjøre applikasjonen som beskrevet i JavaScript.

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
