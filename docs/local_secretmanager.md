## Google Cloud Secret Manager

Noen hemmeligheter lagres i [Secret Manager](https://console.cloud.google.com/security/secret-manager?project=dolly-dev-ff83), som tilsvarer gamle [Vault](https://vault.adeo.no/). Disse hentes automagisk av applikasjonen ved oppstart lokalt gitt at man autentiserer seg med [gcloud CLI](https://cloud.google.com/cli?hl=en) først.

Applikasjonen må ha en dependency på `com.google.cloud:spring-cloud-gcp-starter-secretmanager`. Denne er inkludert ved bruk av plugins [dolly-apps](../plugins/java/src/main/groovy/dolly-apps.gradle) eller [dolly-proxies](../plugins/java/src/main/groovy/dolly-proxies.gradle).

Konfigurasjonen importerer namespace `sm://` og refererer deretter til secrets i ordinær config med `${sm://SECRET_NAME}`. Eks. fra en `application-local.yml`:
```yaml
spring:
  config:
    import: "sm://"
  security:
    oauth2:
      resourceserver:
        aad:
          accepted-audience: ${sm://azure-app-client-id}, api://${sm://azure-app-client-id}
```
Ingen annen kode kreves.