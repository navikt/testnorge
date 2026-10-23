Spring Boot-utgave av [synt-meldekort](https://github.com/navikt/testnorge-syntetiseringspakker/tree/master/apps/arena/synt_meldekort) fra [testnorge-syntetiseringspakker](https://github.com/navikt/testnorge-syntetiseringspakker/tree/master).

Appen genererer syntetiske meldekort med samme algoritme som `bean.model.Model.syn()` i Python-appen. Biblioteket [`libs/synt-bean`](../../libs/synt-bean) leser den eksporterte modellen og trekker verdier kolonne for kolonne i modellens `visitSequence`, med beslutningstrær, kernel density og missing-modeller. Pakken `generator` gjør om radene til meldekort-XML på samme format som Python-appen.

## Modellformat
Appen leser én fil per meldegruppe: `meldekort_bean_<MELDEGRUPPE>.json.gz`. Den stopper ved oppstart hvis en av meldegruppene ARBS, ATTF, DAGP, FY eller INDIV mangler. Filer for andre meldegrupper blir hoppet over.

Filene lages fra Python-picklen `model` med eksportskriptet i `testnorge-syntetiseringspakker`:

Bucketen ligger i `dolly-dev-ff83`. Gi deg selv lese- og skrivetilgang i én time før du starter:

```bash
gcloud projects add-iam-policy-binding dolly-dev-ff83 --member=user:<NAV-EPOST> --role=roles/storage.objectViewer --condition="expression=request.time < timestamp('$(date -v +1H -u +%Y-%m-%dT%H:%M:%SZ)'),title=temp_meldekort_bucket"
gcloud projects add-iam-policy-binding dolly-dev-ff83 --member=user:<NAV-EPOST> --role=roles/storage.objectCreator --condition="expression=request.time < timestamp('$(date -v +1H -u +%Y-%m-%dT%H:%M:%SZ)'),title=temp_meldekort_bucket"
```

```bash
cd testnorge-syntetiseringspakker
mkdir -p /tmp/meldekort && gcloud storage cp gs://synthdata-meldekort-model-bucket/model /tmp/meldekort/model
python3.12 -m venv /tmp/meldekort/venv && /tmp/meldekort/venv/bin/pip install -q -r apps/arena/synt_meldekort/requirements.txt
/tmp/meldekort/venv/bin/python apps/arena/synt_meldekort/scripts/export_bean_model.py --model /tmp/meldekort/model --output-dir /tmp/meldekort/json
gcloud storage cp --no-clobber /tmp/meldekort/json/meldekort_bean_*.json.gz gs://synthdata-meldekort-model-bucket/
```

Skriptet stopper med en feilmelding hvis modellen bruker noe Java-porten ikke støtter.

Testdataene i `libs/synt-bean/src/test/resources/bean` er laget med `create_java_test_fixtures.py` i samme mappe som eksportskriptet. De sammenligner fordelingene fra Java-porten med `Model.syn()` i Python.

## Lokal kjøring
* [Generelt.](../../docs/modules/ROOT/pages/local/local_general.adoc)
* [Modeller.](../../docs/modules/ROOT/pages/local/local_models.adoc) For denne appen legger du `meldekort_bean_*.json.gz` i `./src/main/resources/models/`, ikke `*.onnx`.
