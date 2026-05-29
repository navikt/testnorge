package no.nav.dolly.synt.aap.api;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.synt.aap.dto.AapVedtakDto;
import no.nav.dolly.synt.aap.dto.AatforAaunguforFriMkVedtakDto;
import no.nav.dolly.synt.aap.dto.Vedtak115Dto;
import no.nav.dolly.synt.aap.onnx.OnnxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@DollySpringBootTest
class AapVedtakControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OnnxService onnxService;

    @Test
    void shouldReturn200ForAap() {

        when(onnxService.generateAap(anyList(), anyBoolean()))
                .thenReturn(List.of(AapVedtakDto.builder().vedtaktype("O").build()));

        webTestClient.post()
                .uri("/api/v1/aap")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(Map.of("fraDato", "2018-10-01")))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].VEDTAKTYPE").isEqualTo("O");

    }

    @Test
    void shouldReturn200ForFilteredAap() {

        when(onnxService.generateFilteredAap(anyList(), anyBoolean()))
                .thenReturn(List.of(AapVedtakDto.builder().vedtaktype("O").build()));

        webTestClient.post()
                .uri("/api/v1/aap/filtered")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(Map.of("fraDato", "2018-10-01")))
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void shouldReturn200For115() {

        when(onnxService.generate115(anyList()))
                .thenReturn(List.of(Vedtak115Dto.builder().vedtaktype("O").build()));

        webTestClient.post()
                .uri("/api/v1/11_5")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(Map.of("fraDato", "2018-10-01")))
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void shouldReturn200ForFriMk() {

        when(onnxService.generateFriMk(anyList()))
                .thenReturn(List.of(AatforAaunguforFriMkVedtakDto.builder().vedtaktype("O").build()));

        webTestClient.post()
                .uri("/api/v1/fri_mk")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(Map.of("fraDato", "2018-10-01")))
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void shouldReturn200ForAaungufor() {

        when(onnxService.generateAaungufor(anyList()))
                .thenReturn(List.of(AatforAaunguforFriMkVedtakDto.builder().vedtaktype("O").build()));

        webTestClient.post()
                .uri("/api/v1/aaungufor")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(Map.of("fraDato", "2018-10-01")))
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void shouldReturn200ForAatfor() {

        when(onnxService.generateAatfor(anyList()))
                .thenReturn(List.of(AatforAaunguforFriMkVedtakDto.builder().vedtaktype("O").build()));

        webTestClient.post()
                .uri("/api/v1/aatfor")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(Map.of("fraDato", "2018-10-01")))
                .exchange()
                .expectStatus().isOk();
    }

}
