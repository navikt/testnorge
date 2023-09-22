# Testnorge-Sykemelding-api

API for sykemeldinger.

## Swagger

Swagger finnes under [/api](https://testnorge-sykemelding-api.dev.intern.nav.no/api) -endepunktet til applikasjonen.

## Lokal kjøring

Ha naisdevice kjørende og kjør SykemeldingApiApplicationStarter med følgende argumenter:

```
-Dspring.cloud.vault.token=[kopier token fra vault]
-Dspring.profiles.active=dev
```

### Utviklerimage

I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:

```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
