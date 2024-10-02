# Organisasjon Forvalter

Forvalter som oppretter og deretter deployer organisasjoner basert på innsendte kriterier. Håndterer også status per
orgnr når de sendes videre mot EREG.

## Swagger

Swagger finnes under [/swagger](https://testnav-organisasjon-forvalter.intern.dev.nav.no/swagger) -endepunktet til
applikasjonen.

## Kjør lokalt
* Applikasjonen er avhengig av Vault, se [egen dokumentasjon](../../docs/local_vault.md).
* Applikasjonen er avhengig av en database i GCP, se [egen dokumentasjon](../../docs/gcp_db.md).
* Applikasjonen kjøres med Spring profile `local`.
* Applikasjonen kjøres med VM option `--add-opens java.base/java.lang=ALL-UNNAMED`.
* Swagger tilgjengelig på https://localhost:8080/swagger.