# Testnav-trygdeetaten-nav-proxy
Proxy brukt av aktører fra trygdeetaten tenant for å nå "dolly-backend"-applikasjonen. Applikasjonen kjører med 
trygdeetaten.no tenant (som aktørene bruker) og bytter om til access token for nav.no tenant for å 
nå dolly-applikasjonen.

## Lokal kjøring
* [Generelt.](../../docs/local_general.md)
* [Secret Manager.](../../docs/local_secretmanager.md)

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
7. Fyll inn "dev-gcp.dolly.testnav-trygdeetaten-nav-proxy" som client-id og trykk "Generer on-behalf-of Token"
8. Kopier og bruk det genererte tokenet (brukes som et vanlig Bearer-token)
