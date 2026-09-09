# Template Search Service
Tjeneste som lagrer og henter søkemaler (maler) for Tenor-søk.

Malene er eid av innlogget bruker:
* Azure-brukere ser alle Azure-maler.
* BankID-brukere ser kun maler som eies av brukere i samme organisasjon, slått opp via `testnav-bruker-service`.

Fødselsnummer og d-nummer kan ikke lagres i maler, og valideres bort både i nøkler og verdier.

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)

Lokalt kjører tjenesten mot en H2-database i minnet.
