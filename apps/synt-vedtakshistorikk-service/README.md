# Testnav-synt-vedtakshistorikk-service
Applikasjon for å registrere Testnorge identer som brukere i Arena forvalteren og hente og registrere vedtakshistorikk 
på dem. 

## Lokal kjøring

Start `SyntVedtakshistorikkServiceApplicationStarter` med følgenede props:

```
-Dspring.profiles.active=dev 
-Dspring.cloud.vault.token=<<VAULT_TOKEN>>
```
