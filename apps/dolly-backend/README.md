![Build](https://github.com/navikt/dolly-backend/workflows/Build/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=coverage)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=ncloc)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=navikt_dolly-backend&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=navikt_dolly-backend)

# Dolly-backend

Backend for Dolly som behandler bestillinger og requests fra frontenden og sender disse videre inn i de diverse
systemene hvor testidenter skal ha tilegnet informasjon.

Applikasjonen legger også ved potensielle standard verdier som kreves i API vi er knyttet mot, men som ikke brukerene
trenger å ha noe forhold til under utfylling av bestilling. Noe data blir persistert i postgres db, som f.eks brukerne
av Dolly, bestillingskriterier, hvem som har sendt de inn og status på disse.

## Lokal kjøring

* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)
* [Database i GCP.](../../docs/modules/ROOT/pages/local/gcp_db.adoc)
* [OpenSearch.](../../docs/modules/ROOT/pages/local/local_opensearch.adoc)

Evt midlertidig påloggingssinfo for OpenSearch i lokal kjøring:

>nais opensearch credentials bestillinger --team dolly --environment dev --permission ADMIN --ttl 14d 

## Tidsgrenser ved dokumentinnsending

Frontend laster opp dokumentene i deler til backend. Backend sender deretter store dokumenter i deler til
`testnav-dolly-proxy`, som setter sammen journalposten og sender den til Dokarkiv.
`testnav-joark-dokument-service` brukes til uthenting og er ikke med i innsendingen.

`DokarkivClient.OPERATION_TIMEOUT` er 10 minutter og gjelder behandling av Dokarkiv-bestillingen, inkludert
opplasting til proxy. Den generelle grensen på 30 sekunder er for kort her for filer på 20+ mb.

`DokarkivPostCommand.RESPONSE_TIMEOUT` er 4 minutter og gjelder venting på journalpostsvaret fra proxy.
Initiering og opplasting av hver del beholder HTTP-klientens grense på 30 sekunder.
Journalpostkallet har kortere tidsgrense enn proxy- og Dokarkiv-ingressenes nåværende grense på 300 sekunder.
Andre fagsystemer beholder sine tidsgrenser.