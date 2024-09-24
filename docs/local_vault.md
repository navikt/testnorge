# Hvordan bruke Vault for kjøring lokalt

Enkelte applikasjoner/proxyer er avhengig av tilgang til Vault for å hente ut enkelte properties under lokal kjøring.

Det er 3 måter å konfigurere tilgang til Vault på. Uansett hvilken metode du velger så er token gyldig i 8 timer. Du trenger altså ikke å autentisere deg på nytt før token utgår.

### 1. Bruke Vault CLI.
Avhenger av at du har [Vault CLI](https://developer.hashicorp.com/vault/docs/commands) installert, og har autentisert deg på forhånd vha. `vault login -method=oidc`.
### 2. Sette VM option `spring.cloud.vault.token`.
Avhenger av at du henter token fra [Vault](https://vault.adeo.no/) og oppdaterer runtime configuration med VM option `-Dspring.cloud.vault.token=<TOKEN>`.
### 3. Sette system environment variable `VAULT_TOKEN`.
Avhenger av at du henter token fra Vault og oppdaterer runtime configuration med environment variable `VAULT_TOKEN=<TOKEN>`.

### Prioritering
Vault token hentes i rekkefølgen:
1. Environment variable `VAULT_TOKEN`.
2. VM option `spring.cloud.vault.token`.
3. Vault CLI `vault print token`.

Koden for dette ligger i [AbstractLocalVaultConfiguration](../libs/vault/src/main/java/no/nav/testnav/libs/vault/AbstractLocalVaultConfiguration.java).