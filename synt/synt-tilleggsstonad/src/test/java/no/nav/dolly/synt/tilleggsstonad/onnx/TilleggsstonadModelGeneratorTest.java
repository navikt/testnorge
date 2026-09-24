package no.nav.dolly.synt.tilleggsstonad.onnx;

import no.nav.dolly.synt.tilleggsstonad.dto.VedtakRequestDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("This test is dependent on legacy model metadata and is not suitable for automated testing")
class TilleggsstonadModelGeneratorTest {

    private final TilleggsstonadModelGenerator generator = new TilleggsstonadModelGenerator();

    @Test
    void shouldGenerateBoutgiftUsingLegacyMetadata() {
        var result = generator.generateVedtak(
                TilleggsstonadType.BOUTGIFT,
                List.of(VedtakRequestDto.builder()
                        .fraDato("2018-10-01")
                        .tilDato("2018-12-01")
                        .utfall("JA")
                        .vedtakDato("2018-10-15")
                        .vedtakTypeKode("O")
                        .build()),
                true
        );

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).containsEntry("RETTIGHET_KODE", "TSOBOUTG");
        assertThat(result.getFirst()).containsKeys("BOUTGIFTER", "VEDTAKSPERIODE", "UTFALL");
        assertThat(result.getFirst().get("BOUTGIFTER")).isInstanceOf(List.class);
        assertThat(result.getFirst().get("VEDTAKSPERIODE")).isInstanceOf(Map.class);
    }

    @Test
    void shouldWrapTilsynBarnPayload() {
        var result = generator.generateVedtak(
                TilleggsstonadType.TILSYN_BARN,
                List.of(VedtakRequestDto.builder()
                        .fraDato("2018-10-01")
                        .tilDato("2018-12-01")
                        .utfall("JA")
                        .vedtakDato("2018-10-15")
                        .vedtakTypeKode("O")
                        .build()),
                true
        );

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).containsEntry("RETTIGHET_KODE", "TSOTILBARN");
        assertThat(result.getFirst().get("TILSYN_BARN")).isInstanceOf(List.class);
        var tilsynBarn = (List<?>) result.getFirst().get("TILSYN_BARN");
        assertThat(tilsynBarn).isNotEmpty();
        assertThat(tilsynBarn.getFirst()).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        var barnWrapper = (Map<String, Object>) tilsynBarn.getFirst();
        assertThat(barnWrapper).containsKey("BARN");
    }

}
