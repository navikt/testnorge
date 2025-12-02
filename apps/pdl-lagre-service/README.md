# PDL-lagre-service

Tjeneste som tar imot Kafka-meldinger med oppdateringer fra PDL og lagrer disse i instansene 'pdl-person' og 'pdl-adresse'
på OpenSearch hhv for person og adresse.

For å reindeksere og bygge instansene på nytt stoppes Kafka-listeneren med navn
hhv pdl-lagre-person og pdl-lagre-adresse.

Dernest kalles tilsvarende rest-endepunkt for å slette tilhørende indeks for person eller adresse.

For at Kafka-listeneren skal starte på nytt er det enklest å endre groupId til en annen verdi i hhv.
PdlPersonDokumentListener.java eller PdlAdresseListener.java og deploye på nytt.

Ved oppstart opprettes manglende indekser, samt at disse konfigures fra json-ressursfiler.

## Lokal kjøring

* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Secret Manager.](../../docs/modules/ROOT/pages/local/local_secretmanager.adoc)
* [Database i GCP.](../../docs/modules/ROOT/pages/local/gcp_db.adoc)
* [OpenSearch.](../../docs/modules/ROOT/pages/local/local_opensearch.adoc)

Midlertidig påloggingssinfo for OpenSearch i lokal kjøring:

>nais aiven create opensearch ignored dolly -i bestillinger -a admin -s \<ownsecretname\> -e 10

>nais aiven get opensearch \<ownsecretname\> dolly

Tilsvarende skal det være mulig å hente kafka tilgang via nais aiven create kafka uten at dette har vært forsøkt.