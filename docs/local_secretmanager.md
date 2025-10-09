## Google Cloud Secret Manager

Noen hemmeligheter lagres i [Secret Manager](https://console.cloud.google.com/security/secret-manager?project=dolly-dev-ff83), som tilsvarer gamle [Vault](https://vault.adeo.no/). Disse hentes automagisk av applikasjonen ved oppstart lokalt gitt at man autentiserer seg med [gcloud CLI](https://cloud.google.com/cli?hl=en) først.

Om du ikke har brukt gcloud CLI før må du angi hvilket prosjekt du skal jobbe på. For Dolly er dette `dolly-dev-ff83`:
```
> gcloud config set project dolly-dev-ff83
```
Innloggingen gjøres med:
```
> gcloud auth login --update-adc
```
> NB: Det er viktig å få med seg `--update-adc` her. [Om ADC](https://cloud.google.com/docs/authentication/application-default-credentials).

Alternativt kan man installere [NAIS CLI](https://doc.nais.io/operate/cli/), som gir en wrapper rundt `gcloud auth login --update-adc`:
```
> nais login
```
Applikasjonen må ha en dependency på `com.google.cloud:spring-cloud-gcp-starter-secretmanager`. Denne er inkludert ved bruk av plugins [dolly-apps](../plugins/java/src/main/groovy/dolly-apps.gradle) eller [dolly-proxies](../plugins/java/src/main/groovy/dolly-proxies.gradle).

La Spring Boot-config'en din importere namespace `sm\://` og referer til secrets med `${sm\://SECRET_NAME}`. Eks. fra en `application-local.yml`:
```yaml
AZURE_APP_CLIENT_ID: ${sm\://azure-app-client-id}
AZURE_APP_CLIENT_SECRET: ${sm\://azure-app-client-secret}

spring:
  config:
    import: "sm://"
```
Ingen annen kode kreves.