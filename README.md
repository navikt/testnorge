![Build](https://github.com/navikt/dolly-backend/workflows/Build/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=coverage)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=ncloc)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)

# dolly-backend

## Avhenighetsanalyse

**Kan kun brukes fra utviklerimage**
https://dolly-backend.nais.preprod.local/swagger-ui.html

## Kjør lokalt

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

Så kjør `mvn clean install`

## Deploy status

![Deploy dev t1](https://github.com/navikt/dolly-backend/workflows/Deploy%20dev%20t1/badge.svg)
![Deploy dev t2](https://github.com/navikt/dolly-backend/workflows/Deploy%20dev%20t2/badge.svg)
![Deploy dev u2](https://github.com/navikt/dolly-backend/workflows/Deploy%20dev%20u2/badge.svg)
![Deploy dev default](https://github.com/navikt/dolly-backend/workflows/Deploy%20dev%20default/badge.svg)
