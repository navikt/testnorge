package no.nav.dolly.synt.dagpenger.api;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.synt.dagpenger.dto.DagpengevedtakDto;
import no.nav.dolly.synt.dagpenger.onnx.OnnxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.format.DateTimeParseException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DollySpringBootTest
class DagpengevedtakControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OnnxService onnxService;

    @Test
    void shouldReturn200WithVedtakForDago() {

        var vedtak = DagpengevedtakDto.builder().rettighetKode("DAGO").build();
        when(onnxService.generateVedtak(eq("DAGO"), any()))
                .thenReturn(List.of(vedtak));

        webTestClient
                .post()
                .uri("/api/v1/vedtak/DAGO")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of("2018-10-01"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].rettighetKode").isEqualTo("DAGO");

    }

    @Test
    void shouldReturn200WithVedtakForPerm() {

        var vedtak = DagpengevedtakDto.builder().rettighetKode("PERM").build();
        when(onnxService.generateVedtak(eq("PERM"), any()))
                .thenReturn(List.of(vedtak));

        webTestClient
                .post()
                .uri("/api/v1/vedtak/PERM")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of("2019-05-01"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].rettighetKode").isEqualTo("PERM");

    }

    @Test
    void shouldReturn200WithEmptyListForEmptyInput() {

        when(onnxService.generateVedtak(any(), eq(List.of())))
                .thenReturn(List.of());

        webTestClient
                .post()
                .uri("/api/v1/vedtak/DAGO")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DagpengevedtakDto.class)
                .hasSize(0);

    }

    @Test
    void shouldReturnOneVedtakPerInputDate() {

        var dates = List.of("2018-10-01", "2019-01-01", "2020-06-15");
        var vedtakListe = List.of(
                DagpengevedtakDto.builder().rettighetKode("DAGO").build(),
                DagpengevedtakDto.builder().rettighetKode("DAGO").build(),
                DagpengevedtakDto.builder().rettighetKode("DAGO").build()
        );
        when(onnxService.generateVedtak(eq("DAGO"), eq(dates)))
                .thenReturn(vedtakListe);

        webTestClient
                .post()
                .uri("/api/v1/vedtak/DAGO")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dates)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DagpengevedtakDto.class)
                .hasSize(3);

    }

    @Test
    void shouldReturn500WhenRettighetTypeIsInvalid() {

        when(onnxService.generateVedtak(any(), any()))
                .thenThrow(new IllegalArgumentException("No enum constant for INVALID"));

        webTestClient
                .post()
                .uri("/api/v1/vedtak/INVALID")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of("2018-10-01"))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Inference failure");

    }

    @Test
    void shouldReturn400WhenInputDateCannotBeParsed() {

        when(onnxService.generateVedtak(any(), any()))
                .thenThrow(new DateTimeParseException("Invalid date format", "not-a-date", 0));

        webTestClient
                .post()
                .uri("/api/v1/vedtak/DAGO")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of("not-a-date"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Invalid request");

    }

}

