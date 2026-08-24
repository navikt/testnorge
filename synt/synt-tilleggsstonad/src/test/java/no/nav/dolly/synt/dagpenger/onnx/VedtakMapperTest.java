package no.nav.dolly.synt.dagpenger.onnx;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class VedtakMapperTest {

    @Test
    void shouldSetRettighetKodeFromPrediction() {

        var prediction = getPrediction("PERM", "2024-01-15", Map.of("labelA", 0.9));
        var dto = VedtakMapper.fromPrediction(prediction);

        assertThat(dto.getRettighetKode())
                .isEqualTo("PERM");

    }

    @Test
    void shouldSetPermSpecificFlagsWhenRettighetIsPerm() {

        var prediction = getPrediction("PERM", "2024-01-15", Map.of("labelA", 0.8));
        var dto = VedtakMapper.fromPrediction(prediction);

        assertThat(dto.getDagpengeperiode().getEndringPermitteringsteller())
                .isEqualTo("J");
        assertThat(dto.getDagpengeperiode().getAntallUkerPermittering())
                .isGreaterThan(0);
        assertThat(dto.getPermittering()).anySatisfy(kv -> assertThat(kv.getVerdi())
                .isEqualTo("J"));

    }

    @Test
    void shouldSetNonPermFlagsWhenRettighetIsDago() {

        var prediction = getPrediction("DAGO", "2024-03-01", Map.of("labelX", 0.7));
        var dto = VedtakMapper.fromPrediction(prediction);

        assertThat(dto.getDagpengeperiode().getEndringPermitteringsteller())
                .isEqualTo("N");
        assertThat(dto.getDagpengeperiode().getAntallUkerPermittering())
                .isEqualTo(0);
        assertThat(dto.getPermittering()).anySatisfy(kv -> assertThat(kv.getVerdi())
                .isEqualTo("N"));
        assertThat(dto.getTaptArbeidstid().getNaavaerendeArbeidstid())
                .isEqualTo(0.0);

    }

    @Test
    void shouldSetVedtaksperiodeTo26WeeksAfterDatoMottatt() {

        var prediction = getPrediction("DAGO", "2024-01-01", Map.of("label", 0.5));
        var dto = VedtakMapper.fromPrediction(prediction);

        assertThat(dto.getVedtaksperiode().getFom())
                .isEqualTo("2024-01-01");
        assertThat(dto.getVedtaksperiode().getTom())
                .isEqualTo(LocalDate.parse("2024-01-01").plusWeeks(26).toString());

    }

    @Test
    void shouldDeriveBruktInntektsperiodeFromDatoMottatt() {

        var prediction = getPrediction("DAGO", "2024-06-01", Map.of("label", 0.5));
        var dto = VedtakMapper.fromPrediction(prediction);

        assertThat(dto.getBruktInntektsperiode().getForsteManed())
                .isEqualTo("2023-06-01");
        assertThat(dto.getBruktInntektsperiode().getSisteManed())
                .isEqualTo("2024-05-01");

    }

    @Test
    void shouldDeriveAntallUkerFromHighestProbability() {

        var highProb = getPrediction("DAGO", "2024-01-01", Map.of("high", 1.0));
        var lowProb = getPrediction("DAGO", "2024-01-01", Map.of("low", 0.0));

        // Math.max(1, Math.round(10 + (1.0 * 42))) = 52
        assertThat(VedtakMapper.fromPrediction(highProb).getDagpengeperiode().getAntallUker())
                .isEqualTo(52);
        // Math.max(1, Math.round(10 + (0.0 * 42))) = 10
        assertThat(VedtakMapper.fromPrediction(lowProb).getDagpengeperiode().getAntallUker())
                .isEqualTo(10);

    }

    @Test
    void shouldDerivePermNaavaerendeArbeidstidFromHighestProbability() {

        var fullPermittering = getPrediction("PERM", "2024-01-01", Map.of("label", 1.0));
        var halvPermittering = getPrediction("PERM", "2024-01-01", Map.of("label", 0.5));

        // Math.round((37.5 - (1.0 * 37.5)) * 10) / 10.0 = 0.0
        assertThat(VedtakMapper.fromPrediction(fullPermittering).getTaptArbeidstid().getNaavaerendeArbeidstid())
                .isEqualTo(0.0);
        // Math.round((37.5 - (0.5 * 37.5)) * 10) / 10.0 = 18.8
        assertThat(VedtakMapper.fromPrediction(halvPermittering).getTaptArbeidstid().getNaavaerendeArbeidstid())
                .isEqualTo(18.8);

    }

    private static GeneratedDagpengevedtak getPrediction(String rettighetKode, String datoMottatt, Map<String, Double> probabilities) {
        return new GeneratedDagpengevedtak(
                rettighetKode,
                "NY_RETTIGHET",
                datoMottatt,
                datoMottatt,
                "model.onnx",
                probabilities.keySet().iterator().next(),
                probabilities.values().iterator().next(),
                probabilities
        );
    }

}

