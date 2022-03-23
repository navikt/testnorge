# Inntektsmelding generator

Microservice for å mappe inntektsmeldinger i JSON format til XML format basert på [kodeverk](https://github.com/navikt/tjenestespesifikasjoner/blob/master/nav-altinn-inntektsmelding/src/main/xsd/).  
Foreløpig er bare 201812 format støttet.

## Swagger
Swagger finnes under [/swagger](https://testnav-inntektsmelding-generator-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør InntektsmeldingGeneratorApplicationStarter med følgende argumenter:
```
-Dspring.cloud.vault.token=[Copy token fra Vault]
-Dspring.profiles.active=dev
```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
