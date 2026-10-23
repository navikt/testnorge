package no.nav.dolly.synt.tilleggsstonad.api;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.synt.tilleggsstonad.generator.TilleggsstonadService;
import no.nav.dolly.synt.tilleggsstonad.generator.TilleggsstonadType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@DollySpringBootTest
class TilleggsstonadControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TilleggsstonadService tilleggsstonadService;

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/boutgift",
            "/api/v1/boutgifter_arbeidssokere",
            "/api/v1/daglig_reise",
            "/api/v1/daglig_reise_arbeidssoker",
            "/api/v1/laeremidler",
            "/api/v1/laeremidler_arbeidssokere",
            "/api/v1/flytting",
            "/api/v1/flytting_arbeidssokere",
            "/api/v1/reise_aktivitet_og_hjemreiser",
            "/api/v1/reise_aktivitet_og_hjemreiser_arbeidssokere",
            "/api/v1/reisestonad_til_arbeidssokere",
            "/api/v1/reise_til_obligatorisk_samling",
            "/api/v1/reise_til_obligatorisk_samling_arbeidssokere",
            "/api/v1/tilsyn_familiemedlemmer",
            "/api/v1/tilsyn_familiemedlemmer_arbeidssokere",
            "/api/v1/tilsyn_barn",
            "/api/v1/tilsyn_barn_arbeidssoker"
    })
    void shouldReturn200ForAllSupportedEndpoints(String endpoint) {

        when(tilleggsstonadService.generateVedtak(any(), anyList(), anyBoolean()))
                .thenReturn(List.of(Map.of("VEDTAKTYPE", "O")));

        webTestClient.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(Map.of("fraDato", "2018-10-01")))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].VEDTAKTYPE").isEqualTo("O");

    }

    @ParameterizedTest
    @CsvSource({
            "'', true",
            "?tilDato=true, true",
            "?tilDato=false, false",
            "?tilDato=False, true",
            "?tilDato=TRUE, true"
    })
    void shouldUseSubmittedTilDatoUnlessQueryParameterIsLowercaseFalse(String query, boolean brukInnsendtTilDato) {

        when(tilleggsstonadService.generateVedtak(any(), anyList(), anyBoolean()))
                .thenReturn(List.of(Map.of("VEDTAKTYPE", "O")));

        webTestClient.post()
                .uri("/api/v1/boutgift" + query)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(Map.of("fraDato", "2018-10-01")))
                .exchange()
                .expectStatus().isOk();

        verify(tilleggsstonadService).generateVedtak(eq(TilleggsstonadType.BOUTGIFT), anyList(), eq(brukInnsendtTilDato));

    }

    @Test
    void shouldReturn400ForInvalidTilDatoQueryParameter() {

        webTestClient.post()
                .uri("/api/v1/boutgift?tilDato=ja")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(Map.of("fraDato", "2018-10-01")))
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(tilleggsstonadService);

    }

    @Test
    void shouldReturnJsonWhenLegacyClientAcceptsTextPlain() {

        when(tilleggsstonadService.generateVedtak(any(), anyList(), anyBoolean()))
                .thenReturn(List.of(Map.of("VEDTAKTYPE", "O")));

        webTestClient.post()
                .uri("/api/v1/boutgift")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN)
                .bodyValue(List.of(Map.of("fraDato", "2018-10-01")))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].VEDTAKTYPE").isEqualTo("O");

    }

    @Test
    void shouldReturn400WhenGeneratorRejectsRequest() {

        when(tilleggsstonadService.generateVedtak(any(), anyList(), anyBoolean()))
                .thenThrow(new IllegalArgumentException("Vedtak av type E kan ikke komme før et vedtak av type O"));

        webTestClient.post()
                .uri("/api/v1/boutgift")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(Map.of("fraDato", "2018-10-01", "vedtakTypeKode", "E")))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.details").isEqualTo("Vedtak av type E kan ikke komme før et vedtak av type O");

    }

}
