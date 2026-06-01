package no.nav.dolly.synt.aap.onnx;

import no.nav.dolly.synt.aap.dto.AapVedtakDto;
import no.nav.dolly.synt.aap.dto.VedtakRequestDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AapHistoryFilterTest {

    @Test
    void shouldRemoveIllogicalRequestsAfterStansMedJa() {

        var requests = List.of(
                VedtakRequestDto.builder().vedtakTypeKode("O").utfall("JA").build(),
                VedtakRequestDto.builder().vedtakTypeKode("S").utfall("JA").build(),
                VedtakRequestDto.builder().vedtakTypeKode("E").utfall("JA").build(),
                VedtakRequestDto.builder().vedtakTypeKode("S").utfall("NEI").build()
        );
        var filtered = AapHistoryFilter.removeIllogicalRequests(requests);

        assertThat(filtered)
                .hasSize(2);
        assertThat(filtered.get(0).getVedtakTypeKode())
                .isEqualTo("O");
        assertThat(filtered.get(1).getVedtakTypeKode())
                .isEqualTo("S");

    }

    @Test
    void shouldPostprocessForHistoryByResettingOldAvbruddAndRemovingFollowingNonOVedtak() {

        var oldDate = LocalDate.now().minusDays(30).toString();
        var generated = List.of(
                AapVedtakDto.builder().fraDato(oldDate).avbruddskode("A1").vedtaktype("S").utfall("JA").build(),
                AapVedtakDto.builder().fraDato(LocalDate.now().toString()).vedtaktype("E").utfall("JA").build(),
                AapVedtakDto.builder().fraDato(LocalDate.now().toString()).vedtaktype("O").utfall("JA").build()
        );

        var processed = AapHistoryFilter.postprocessForUseInHistory(generated);

        assertThat(processed)
                .hasSize(3);
        assertThat(processed.getFirst().getAvbruddskode())
                .isEmpty();

    }

    @Test
    void shouldShiftDatesForNonOVedtakWhenStartDateIsBeforePreviousStart() {

        var generated = List.of(
                AapVedtakDto.builder().fraDato("2024-01-10").tilDato("").vedtaktype("O").utfall("JA").build(),
                AapVedtakDto.builder().fraDato("2024-01-09").tilDato("2024-01-20").
                        datoMottatt("2024-01-09").vedtakDato("2024-01-09").
                        vedtaktype("S").utfall("JA").build()
        );

        var processed = AapHistoryFilter.postprocessForUseInHistory(generated);

        assertThat(processed.get(1).getFraDato())
                .isEqualTo("2024-01-11");
        assertThat(processed.get(1).getTilDato())
                .isEqualTo("2024-01-22");
        assertThat(processed.get(1).getDatoMottatt())
                .isEqualTo("2024-01-11");
        assertThat(processed.get(1).getVedtakDato())
                .isEqualTo("2024-01-11");

    }

    @Test
    void shouldRemoveFollowingNonOVedtakAfterRecentAvbrudd() {

        var recentDate = LocalDate.now().minusDays(1).toString();
        var generated = List.of(
                AapVedtakDto.builder().fraDato(recentDate).avbruddskode("A1").vedtaktype("S").utfall("JA").build(),
                AapVedtakDto.builder().fraDato(LocalDate.now().toString()).vedtaktype("E").utfall("JA").build(),
                AapVedtakDto.builder().fraDato(LocalDate.now().toString()).vedtaktype("O").utfall("JA").build()
        );

        var processed = AapHistoryFilter.postprocessForUseInHistory(generated);

        assertThat(processed).hasSize(2);
        assertThat(processed.get(0).getAvbruddskode()).isEqualTo("A1");
        assertThat(processed.get(1).getVedtaktype()).isEqualTo("O");

    }
}
