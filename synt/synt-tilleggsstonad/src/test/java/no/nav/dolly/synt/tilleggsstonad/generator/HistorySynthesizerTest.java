package no.nav.dolly.synt.tilleggsstonad.generator;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.SplittableRandom;

import static no.nav.dolly.synt.tilleggsstonad.generator.TilleggsstonadFixtures.request;
import static org.assertj.core.api.Assertions.assertThat;

class HistorySynthesizerTest {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    private static final String MODEL = """
            {
              "formatVersion": 1,
              "model": "kde",
              "root": "FRA_DATO",
              "deltaColumns": ["TIL_DATO"],
              "beanModels": {
                "O": {"JA": {"formatVersion": 1, "name": "kde/O/JA", "strictBoundaries": true, "visitSequence": [], "columns": [
                  %s, %s, %s, %s, %s
                ]}},
                "E": {"JA": {"formatVersion": 1, "name": "kde/E/JA", "strictBoundaries": true, "visitSequence": [], "columns": [
                  %s, %s, %s, %s, %s
                ]}}
              },
              "kde": {
                "POSITIV": {"kernel": "gaussian", "bandwidth": 1e-12, "points": [1.239], "weights": null},
                "NEGATIV": {"kernel": "gaussian", "bandwidth": 1e-12, "points": [-0.5], "weights": null},
                "MANGLER": {"kernel": "gaussian", "bandwidth": 1e-12, "points": [2.0], "weights": null},
                "TEKST": {"kernel": "gaussian", "bandwidth": 1e-12, "points": [2.0], "weights": null}
              }
            }
            """.formatted(
            column("VEDTAKTYPE", "\"O\""), column("POSITIV", "\"100\""), column("NEGATIV", "\"100\""), column("MANGLER", "null"), column("TEKST", "\"abc\""),
            column("VEDTAKTYPE", "\"0\""), column("POSITIV", "\"1\""), column("NEGATIV", "\"0\""), column("MANGLER", "\"0\""), column("TEKST", "\"0\""));

    @Test
    void shouldMultiplyKernelDensityColumnsWithTruncatedNonNegativeSample() {

        var model = new TilleggsstonadModel(JSON_MAPPER.readValue(MODEL, TilleggsstonadModelDocument.class));

        var historikk = HistorySynthesizer.synthesize(model, TilleggsstonadType.HistorySynthesis.STANDARD_WITH_KERNEL_DENSITY,
                List.of(request("O", "JA", "2025-01-01", "2025-06-30"), request("E", "JA", "2025-02-01", "2025-06-30")),
                new SplittableRandom(1));

        assertThat(historikk.get(0)).containsEntry("POSITIV", 123.0).containsEntry("NEGATIV", 0.0)
                .containsEntry("MANGLER", null).containsEntry("TEKST", null).containsEntry("VEDTAKTYPE", "O");
        assertThat(historikk.get(1).get("POSITIV")).isEqualTo(123.0);
        assertThat(historikk.get(1)).containsEntry("NEGATIV", 0.0).containsEntry("VEDTAKTYPE", "E");

    }

    private static String column(String name, String value) {

        return """
                {"name": "%s", "datatype": "tekst", "categorical": true, "integerType": true, "labels": [%s],
                 "excluded": {"raw": [0], "output": [%s]}, "synthesis": null}
                """.formatted(name, value, value);

    }

}
