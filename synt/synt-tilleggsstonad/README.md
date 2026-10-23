Spring Boot-utgave av [synt_tilleggsstonad](https://github.com/navikt/testnorge-syntetiseringspakker/tree/master/apps/arena/synt_tilleggsstonad) fra [testnorge-syntetiseringspakker](https://github.com/navikt/testnorge-syntetiseringspakker/tree/master).

Appen genererer syntetiske tilleggsstønad-vedtak med de samme modellene og den samme etterbehandlingen som Python-appen. Biblioteket [`libs/synt-bean`](../../libs/synt-bean) kjører `bean.model.Model.syn()` på de eksporterte modellene. Pakken `generator` lager vedtakshistorikken, regner om relative datoer, bygger vedtaksstrukturen og sjekker målgruppen slik Python-appen gjør.

## Modellformat
Appen leser én fil per stønadstype: `tilleggsstonad_bean_<modell>.json.gz`, for eksempel `tilleggsstonad_bean_boutgifter.json.gz`. Filnavnene står i `app.config.models` i `application-prod.yml`. Appen stopper ved oppstart hvis en av de 17 filene mangler.

Filene lages fra Python-picklene i bucketen `synthdata-tilleggsstonad-model-bucket` med eksportskriptet i `testnorge-syntetiseringspakker`. Picklene heter det samme som modellen, uten filendelse.

Bucketen ligger i `dolly-dev-ff83`. Gi deg selv lese- og skrivetilgang i én time før du starter:

```bash
gcloud projects add-iam-policy-binding dolly-dev-ff83 --member=user:<NAV-EPOST> --role=roles/storage.objectViewer --condition="expression=request.time < timestamp('$(date -v +1H -u +%Y-%m-%dT%H:%M:%SZ)'),title=temp_tilleggsstonad_bucket"
gcloud projects add-iam-policy-binding dolly-dev-ff83 --member=user:<NAV-EPOST> --role=roles/storage.objectCreator --condition="expression=request.time < timestamp('$(date -v +1H -u +%Y-%m-%dT%H:%M:%SZ)'),title=temp_tilleggsstonad_bucket"
```

```bash
cd testnorge-syntetiseringspakker
mkdir -p apps/arena/synt_tilleggsstonad/models && gcloud storage cp "gs://synthdata-tilleggsstonad-model-bucket/*" apps/arena/synt_tilleggsstonad/models/
python3.12 -m venv /tmp/tilleggsstonad/venv && /tmp/tilleggsstonad/venv/bin/pip install -q -r apps/arena/synt_tilleggsstonad/requirements.txt
/tmp/tilleggsstonad/venv/bin/python apps/arena/synt_tilleggsstonad/scripts/export_bean_model.py --model-dir apps/arena/synt_tilleggsstonad/models --output-dir /tmp/tilleggsstonad/json
gcloud storage cp --no-clobber /tmp/tilleggsstonad/json/tilleggsstonad_bean_*.json.gz gs://synthdata-tilleggsstonad-model-bucket/
```

Skriptet stopper med en feilmelding hvis en modell bruker noe Java-porten ikke støtter.

Testene i `src/test/resources/models` bruker fire av de eksporterte modellene. `reference_tilsyn_familiemedlemmer_*.json` er svaret fra Python-appen for en historikk der modellen bare har én mulig verdi per kolonne, så Java-svaret skal være identisk.

## Avvik fra Python-appen
* Ugyldige forespørsler gir `400 Bad Request` med en feilmelding. Python-appen ga `500`. Det gjelder ugyldige datoer, ukjent vedtaktype eller utfall, kombinasjoner modellen ikke har, og E- eller S-vedtak før første O-vedtak.
* Stønadstyper som trekker på nytt til målgruppen er gyldig, gir `400` for en tom liste. Python-appen hang i en evig løkke. Etter 10 000 forsøk uten gyldig målgruppe gir appen `500`.

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Modeller.](../../docs/modules/ROOT/pages/local/local_models.adoc) For denne appen legger du `tilleggsstonad_bean_*.json.gz` i `./src/main/resources/models/`, ikke `*.onnx`.
