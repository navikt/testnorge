![Build](https://github.com/navikt/dolly-frontend/workflows/Build/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-frontend&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_dolly-frontend)
![Deploy](https://github.com/navikt/dolly-frontend/workflows/Deploy/badge.svg)
![Deploy t1](https://github.com/navikt/dolly-frontend/workflows/Deploy%20t1/badge.svg)
![Deploy t2](https://github.com/navikt/dolly-frontend/workflows/Deploy%20t2/badge.svg)
![Deploy u2](https://github.com/navikt/dolly-frontend/workflows/Deploy%20u2/badge.svg)

# Dolly
Prosjekt for å opprette og konfigurere testpersoner knyttet til fellesregistrene i NAV

## Dokumentasjon
### Swagger
Swagger finnes under /swagger-ui.html.

## Lokal kjøring

#### Javascript
- Logg på [dolly-t2](https://dolly-t2.nais.preprod.local/) for å få oppdatert OIDC token i nettleser.
- Kjør applikasjonen med npm start (fra ./src/main/web_src)

**NB: Legg til i .npmrc filen får å kjøre fra utv image**

```
https-proxy=http://155.55.60.117:8088/
proxy=http://155.55.60.117:8088/
registry=http://registry.npmjs.org/
no-proxy=*.adeo.no
strict-ssl=false
```

#### Java
For å kjøre lokalt (LocalAppStarter) må active profile settes til `dev`. I tillegg, for å kunne gjøre kall mot NAIS apper må nav 
truststore settes opp og cloud vault token må hentes fra Vault. Vault token hentes ved at man logger inn i Vault, 
trykker på nedtrekksmenyen oppe til høyre, og trykker på "Copy token".

Hvis du også kjører Dolly-backend lokalt og vil teste Dolly mot den lokale backend versjonen så må `dolly.url` i application-local.yml 
settes til url-en for den lokale versjonen av dolly-backend (eks: `http://localhost:8080`)

Legg dette i **din** maven settings.xml fil (bytt ut password med ditt Github-token):

```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>NAV internal Nexus</id>
      <mirrorOf>external:*,!nav-github-packages</mirrorOf>
      <url>https://repo.adeo.no/repository/maven-public</url>
    </mirror>
  </mirrors>
  <profiles>
    <profile>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <repositories>
        <repository>
          <id>nav-github-packages</id>
          <url>https://repo.adeo.no/repository/github-package-registry-navikt/</url>
        </repository>
      </repositories>
    </profile>
  </profiles>
      <servers>
        <server>
            <id>nav-github-packages</id>
            <username>token</username>
            <password>...</password>
        </server>
    </servers>
</settings>
```
##### Bygge med maven utenfor utviklerimage:

**NB: `navtunnel` må kjøre**

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
##### I Intellij:

Run -> Edit Configurations -> VM Options 

Fyll inn verdiene:
```
-Djavax.net.ssl.trustStore=C:\path\to\truststore
-Djavax.net.ssl.trustStorePassword=(Passord)
-Dspring.cloud.vault.token=(Copy token fra Vault)
-Dspring.profiles.active=dev
```

#### Begge
Hvis du har gjort endringer lokalt på dolly sin frontend-backend (f.eks: lagt til en ny proxy i ProxyController) og 
vil raskt teste hvordan det fungerer sammen med frontend elementer så kan du gjøre følgende:
- Kjør ApplicationStarter (som forklart under Java)
- Kjør applikasjonen med npm run local (fra ./src/main/web_src)
