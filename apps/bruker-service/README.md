# Bruker-service

Tjenesten utsteder personlige `User-Jwt` for brukere som er autentisert med Azure AD eller BankID via ID-porten og TokenX.
For BankID-brukere validerer tjenesten tilgang til organisasjonen og bruker en hashet bruker-ID.
For Azure-brukere må bruker-ID-en samsvare med den autentiserte brukeren.

Tjenesten slår ikke opp team og legger ikke teamkontekst i tokenet. Tjenester som trenger teameierskap, må avklare dette selv.

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
