# Dolly
Prosjekt for å opprette og konfigurere testpersoner knyttet til fellesregistrene i NAV

Prosjektet kjører med to profiler:

- h2: in-memory h2 database
- oracle: oracle database satt opp utenfor


### Localkjøring
For å kunne gjøre kall mot NAIS apper, må nav truststore settes opp som spesifisert her: 
https://confluence.adeo.no/display/LFEL/Debugging . 

I Intellij: Run -> Edit Configurations -> Fyll in verdier spesifisert i link over