# Inntektsmelding generator
Microservice for å mappe inntektsmeldinger i JSON format til XML format basert
på [kodeverk](https://github.com/navikt/tjenestespesifikasjoner/blob/master/nav-altinn-inntektsmelding/src/main/xsd/).  
Foreløpig er bare 201812 format støttet.

Dette utdaterte biblioteket er brukt for å generere XML, men har blitt manuelt portet til jakarta for å støtte nyere
versjon av rammeverk:

```
    implementation 'no.nav.tjenestespesifikasjoner:nav-altinn-inntektsmelding:1.2019.09.25-00.21-49b69f0625e0'
```

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)
