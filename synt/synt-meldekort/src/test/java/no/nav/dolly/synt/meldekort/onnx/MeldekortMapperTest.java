package no.nav.dolly.synt.meldekort.onnx;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MeldekortMapperTest {

    @Test
    void shouldGenerate14MeldekortDays() {

        var prediction = new OnnxPrediction("model_DAGO_example.onnx", "label", 0.8, Map.of("class_0", 0.8, "class_1", 0.2));
        var result = MeldekortMapper.fromPredictions(MeldekortType.DAGP, 0, java.util.List.of(prediction), null);

        assertThat(result.meldekortDager()).hasSize(14);
        assertThat(result.meldekortDager().getFirst().dag()).isEqualTo(1);
        assertThat(result.meldekortDager().getLast().dag()).isEqualTo(14);

    }

    @Test
    void shouldUseArbeidstimerOverrideForAllDays() {

        var prediction = new OnnxPrediction("model_PERM_example.onnx", "label", 0.5, Map.of("class_0", 0.4, "class_1", 0.6));
        var result = MeldekortMapper.fromPredictions(MeldekortType.ATTF, 1, java.util.List.of(prediction), 12.3);

        assertThat(result.meldekortDager())
                .allSatisfy(day -> assertThat(day.arbeidetTimerSum()).isEqualTo(12.3));

    }

    @Test
    void shouldCreateSoapEnvelopeFromGeneratedMeldekort() {

        var prediction = new OnnxPrediction("model_LONN_example.onnx", "label", 0.9, Map.of("class_0", 0.9, "class_1", 0.1));
        var result = MeldekortMapper.fromPredictions(MeldekortType.ATTF, 2, java.util.List.of(prediction), 0.0);
        var xml = MeldekortXmlMapper.toXml(2, result);

        assertThat(xml).contains("<soapenv:Envelope");
        assertThat(xml).contains("<ns2:MeldekortDager>");
        assertThat(xml).contains("<ns2:Dag>14</ns2:Dag>");
        assertThat(xml).contains("<ns2:Meldegruppe>ATTF</ns2:Meldegruppe>");

    }

}

