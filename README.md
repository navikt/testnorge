# Dolly
Prosjekt for å opprette og konfigurere testpersoner knyttet til fellesregistrene i NAV


### Localkjøring
####Javascript kun
- i config.js påse at:
config.services.dollyBackend = 'https://dolly-u2.nais.preprod.local/api/v1'
- Logg på dolly-u2 for å få oppdatert oidc token i nettleser
- kjør applikasjonen med npm start

####Java
For å kunne gjøre kall mot NAIS apper, må 
- nav truststore settes opp
- cloud vault token hents fra Vault.

I Intellij: Run -> Edit Configurations -> VM Options (Fyll inn truststore verdier) 
og -Dspring.cloud.vault.token=(Copy token fra Vault)

og config.js oppdateres med config.services.dollyBackend = 'http://localhost:8020/api/v1'
