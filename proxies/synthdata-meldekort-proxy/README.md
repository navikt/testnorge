# Testnav-synthdata-meldekort-proxy
Proxy brukt av Arena forvalteren for å nå "synthdata-arena-meldekort"-applikasjonen. Applikasjonen kjører med 
trygdeetaten.no-tenant (som Arena forvalteren bruker) og bytter om til access token for nav.no-tenant for å 
nå synt-applikasjonen.

## Lokal kjøring

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)

## Access token
Siden proxy-en kjører med trygdeetaten tenant kan man ikke bruke oversikt-frontend for å hente access token når man 
ønsker å teste proxy-en. I stedet må man bruke Ida for å generere en On-behalf-of-token. Følgende steg beskriver hvordan
et slik token kan genereres:

1. Logg inn på https://ida.intern.nav.no/
2. Velg og trykk på en av dine ida brukere
3. Trykk på "AAD On-Behalf-Of Token"
4. Trykk på "Logg på Azure AD"
5. Følg innloggingsprosessen
6. Gå tilbake til Ida når du er ferdig innlogget med Ida-brukeren
7. Fyll inn "dev-gcp.dolly.testnav-synthdata-meldekort-proxy" som client-id og trykk "Generer on-behalf-of Token"
8. Kopier og bruk det genererte tokenet (brukes som et vanlig Bearer-token)
