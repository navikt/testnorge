package no.nav.dolly.synt.tilleggsstonad.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SplittableRandom;

import static no.nav.dolly.synt.tilleggsstonad.generator.TilleggsstonadFixtures.generator;
import static no.nav.dolly.synt.tilleggsstonad.generator.TilleggsstonadFixtures.request;
import static no.nav.dolly.synt.tilleggsstonad.generator.TilleggsstonadFixtures.resource;
import static no.nav.dolly.synt.tilleggsstonad.generator.TilleggsstonadType.LAEREMIDLER_ARBEIDSSOKERE;
import static no.nav.dolly.synt.tilleggsstonad.generator.TilleggsstonadType.REISESTONAD_TIL_ARBEIDSSOKERE;
import static no.nav.dolly.synt.tilleggsstonad.generator.TilleggsstonadType.TILSYN_FAMILIEMEDLEMMER;
import static no.nav.dolly.synt.tilleggsstonad.generator.TilleggsstonadType.TILSYN_FAMILIEMEDLEMMER_ARBEIDSSOKERE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TilleggsstonadGeneratorTest {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();
    private static final int RUNS = 200;

    private final TilleggsstonadGenerator generator = generator(
            TILSYN_FAMILIEMEDLEMMER,
            TILSYN_FAMILIEMEDLEMMER_ARBEIDSSOKERE,
            LAEREMIDLER_ARBEIDSSOKERE,
            REISESTONAD_TIL_ARBEIDSSOKERE);
    private final SplittableRandom random = new SplittableRandom(42);

    @ParameterizedTest
    @CsvSource({
            "true, reference_tilsyn_familiemedlemmer_bruk_tildato.json",
            "false, reference_tilsyn_familiemedlemmer_syntetisk_tildato.json"
    })
    void shouldMatchPythonOutputForDeterministicModel(boolean brukInnsendtTilDato, String reference) throws Exception {

        var vedtak = generator.generateVedtak(TILSYN_FAMILIEMEDLEMMER, List.of(
                request("O", "JA", "2025-01-01", "2025-06-30"),
                request("S", "JA", "2025-03-01", "")), brukInnsendtTilDato, random);

        var expected = JSON_MAPPER.writeValueAsString(JSON_MAPPER.readTree(Files.readString(resource("models/" + reference))));
        assertThat(JSON_MAPPER.writeValueAsString(vedtak)).isEqualTo(expected);

    }

    @Test
    void shouldReturnEmptyListForEmptyRequestWithoutMaalgruppeRule() {

        assertThat(generator.generateVedtak(TILSYN_FAMILIEMEDLEMMER, List.of(), true, random)).isEmpty();
        assertThat(generator.generateVedtak(TILSYN_FAMILIEMEDLEMMER_ARBEIDSSOKERE, List.of(), true, random)).isEmpty();

    }

    @Test
    void shouldRejectEmptyRequestForTypesThatRetryMaalgruppe() {

        assertThatThrownBy(() -> generator.generateVedtak(REISESTONAD_TIL_ARBEIDSSOKERE, List.of(), true, random))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void shouldRejectEndringBeforeOrdinaer() {

        var requests = List.of(request("E", "JA", "2025-01-01", "2025-06-30"));

        assertThatThrownBy(() -> generator.generateVedtak(LAEREMIDLER_ARBEIDSSOKERE, requests, true, random))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("type O");

    }

    @ParameterizedTest
    @CsvSource({
            "REISESTONAD_TIL_ARBEIDSSOKERE, E, JA",
            "TILSYN_FAMILIEMEDLEMMER, E, JA",
            "TILSYN_FAMILIEMEDLEMMER, S, NEI",
            "LAEREMIDLER_ARBEIDSSOKERE, X, JA",
            "LAEREMIDLER_ARBEIDSSOKERE, O, KANSKJE",
            "REISESTONAD_TIL_ARBEIDSSOKERE, , JA",
            "TILSYN_FAMILIEMEDLEMMER, , JA",
            "LAEREMIDLER_ARBEIDSSOKERE, , JA",
            "LAEREMIDLER_ARBEIDSSOKERE, O, "
    })
    void shouldRejectUnsupportedVedtaktypeOrUtfall(TilleggsstonadType type, String vedtaktype, String utfall) {

        var requests = List.of(request("O", "JA", "2025-01-01", "2025-06-30"), request(vedtaktype, utfall, "2025-02-01", "2025-06-30"));

        assertThatThrownBy(() -> generator.generateVedtak(type, requests, true, random))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @ParameterizedTest
    @CsvSource({
            "01.01.2025, 2025-06-30",
            "2025-02-30, 2025-06-30",
            "2025-01-01, 30.06.2025"
    })
    void shouldRejectInvalidDates(String fraDato, String tilDato) {

        var requests = List.of(request("O", "JA", fraDato, tilDato));

        assertThatThrownBy(() -> generator.generateVedtak(TILSYN_FAMILIEMEDLEMMER, requests, true, random))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void shouldRetryUntilMaalgruppeIsValid() {

        var requests = List.of(request("O", "JA", "2025-01-01", "2025-06-30"), request("O", "NEI", "2025-07-01", "2025-12-31"));

        for (int run = 0; run < RUNS; run++) {
            var vedtak = generator.generateVedtak(REISESTONAD_TIL_ARBEIDSSOKERE, requests, true, random);
            assertThat(vedtak).extracting(v -> v.get("MAALGRUPPEKODE")).doesNotContain("ENSFORUTD", "MOTDAGPEN");
        }

    }

    @Test
    void shouldReplaceInvalidMaalgruppe() {

        var requests = List.of(
                request("O", "JA", "2025-01-01", "2025-06-30"),
                request("E", "JA", "2025-03-01", "2025-06-30"),
                request("S", "JA", "2025-05-01", ""));

        for (int run = 0; run < RUNS; run++) {
            var vedtak = generator.generateVedtak(LAEREMIDLER_ARBEIDSSOKERE, requests, true, random);
            assertThat(vedtak).extracting(v -> v.get("MAALGRUPPEKODE"))
                    .doesNotContain("ENSFORARBS", "GJENEKARBS", "MOTDAGPEN", "ARBSOKERE", "MOTTILTPEN");
        }

    }

    @Test
    void shouldBuildStandardHistoryWithVedtaksperiodeAndStans() {

        var requests = List.of(
                request("O", "JA", "2025-01-01", "2025-06-30"),
                request("E", "JA", "2025-03-01", "2025-06-30"),
                request("S", "JA", "2025-05-01", ""));

        for (int run = 0; run < RUNS; run++) {
            var vedtak = generator.generateVedtak(LAEREMIDLER_ARBEIDSSOKERE, requests, true, random);

            assertThat(vedtak).extracting(v -> v.get("VEDTAKTYPE")).containsExactly("O", "E", "S");
            assertThat(vedtak).extracting(v -> v.get("RETTIGHET_KODE")).containsOnly("TSRLMIDLER");
            assertThat(vedtak.get(0).get("VEDTAKSPERIODE")).isEqualTo(Map.of("FOM", "2025-01-01", "TOM", "2025-06-30"));
            assertThat(vedtak.get(1).get("VEDTAKSPERIODE")).isEqualTo(Map.of("FOM", "2025-03-01", "TOM", "2025-06-30"));
            assertThat(vedtak.get(2).get("VEDTAKSPERIODE")).isEqualTo(Map.of("FOM", "2025-05-01", "TOM", ""));
            vedtak.forEach(v -> {
                assertThat(v.keySet()).doesNotContain("FRA_DATO", "TIL_DATO", "VEDTAKSTATUS");
                assertThat(v.keySet()).containsAll(Set.of("VILKAAR", "LAEREMIDLER", "VEDTAKSPERIODE", "MAALGRUPPEKODE"));
                assertThat(List.copyOf(v.keySet())).isSorted();
            });
        }

    }

    @Test
    void shouldFailFastWhenModelFileIsMissing(@TempDir Path directory) {

        assertThatThrownBy(() -> TilleggsstonadGenerator.fromDirectory(directory))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("tilleggsstonad_bean_");

    }

}
