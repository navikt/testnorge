## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)

## Store dokumenter

Dokarkiv-ruten erstatter opplastingsreferanser med dokumentinnhold og serialiserer journalposten direkte
til UTF-8-bytes. Dermed unngår den å bygge hele journalposten som en ekstra streng før sending.
En fil på 40 MiB blir omtrent 53,3 MiB som Base64, og flere miljøer kan sende slike filer samtidig.

JVM-en bruker inntil 60 % av containerens minne til heap. Med minnegrensen på 2 GiB gir det omtrent
1,2 GiB heap, mot standardgrensen på 512 MiB. Resten er tilgjengelig for blant annet nettverksbuffere,
tråder og metaspace. `ExitOnOutOfMemoryError` stopper JVM-en ved minnemangel, slik at Kubernetes kan
starte den på nytt i stedet for at kall blir hengende til de får timeout.