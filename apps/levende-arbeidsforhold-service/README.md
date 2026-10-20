# Levende arbeidsforhold-service

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)

Avro 1.12.2 krever eksplisitt tillit til Leesah-klassene. Docker-imaget, `bootRun` og Gradle-testene setter
`org.apache.avro.SERIALIZABLE_PACKAGES=no.nav.person.pdl.leesah` ved JVM-oppstart.
Ved direkte kjøring fra IDE må du legge til følgende VM-opsjon:

```text
-Dorg.apache.avro.SERIALIZABLE_PACKAGES=no.nav.person.pdl.leesah
```

Tillatelsen omfatter Leesah-pakken og underpakkene, ikke vilkårlige klasser. Ikke erstatt pakken med `*`.
