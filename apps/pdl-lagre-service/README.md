# PDL-lagre-service

Tjeneste som tar imot Kafka-meldinger med oppdateringer fra PDL og lagrer disse i instansene 'pdl-person' og 'pdl-adresse'
på OpenSearch hhv for person og adresse.

For å reindeksere og bygge instansene på nytt stoppes Kafka-listeneren med navn
hhv pdl-lagre-person og pdl-lagre-adresse.

Dernest kalles tilsvarende rest-endepunkt for å slette tilhørende indeks for person eller adresse.

For at Kafka-listeneren skal starte på nytt er det enklest å endre groupId til en annen verdi i hhv.
PdlPersonDokumentListener.java eller PdlAdresseListener.java og deploye på nytt.

Ved deploy opprettes ny indeks automatisk og innhold bygges opp igjen.

## Lokal kjøring
Har ikke lokal kjøring konfigurert enda.
