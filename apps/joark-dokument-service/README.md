# testnav-joark-dokument-service
App for å hente ut joark dokumenter.

SAF-klienten har en lokal buffergrense på 50 MiB. Den settes med `WebClient.Builder.codecs`
slik at den overstyrer den arvede grensen på 32 MiB, uten å endre grensen for andre klienter.

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)