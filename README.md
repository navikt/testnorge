# Dolly
Prosjekt for å opprette og konfigurere testpersoner knyttet til fellesregistrene i NAV

Prosjektet kjører med to profiler:

- h2: in-memory h2 database
- oracle: oracle database satt opp utenfor


### Localkjøring
For å kunne gjøre kall mot NAIS apper, må 
- nav truststore settes opp
- cloud vault token hents fra Vault.

I Intellij: Run -> Edit Configurations -> VM Options (Fyll inn truststore verdier) 
og -Dspring.cloud.vault.token=(Copy token fra Vault)
