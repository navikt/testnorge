package no.nav.dolly.synt.tilleggsstonad.api;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.synt.tilleggsstonad.onnx.OnnxService;
import org.junit.jupiter.params.ParameterizedTest;
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
import static org.mockito.Mockito.when;

@DollySpringBootTest
class TilleggsstonadControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OnnxService onnxService;

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

        when(onnxService.generateVedtak(any(), anyList(), anyBoolean()))
                .thenReturn(List.of(Map.of("VEDKTAKTYPE", "O")));

        webTestClient.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(Map.of("fraDato", "2018-10-01")))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].VEDKTAKTYPE").isEqualTo("O");
    }

}
