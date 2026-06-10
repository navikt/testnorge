package no.nav.dolly.synt.meldekort.api;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.synt.meldekort.onnx.MeldekortType;
import no.nav.dolly.synt.meldekort.onnx.OnnxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@DollySpringBootTest
class MeldekortControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OnnxService onnxService;

    @Test
    void shouldReturn200WithGeneratedMeldekort() {

        when(onnxService.generateMeldekort(MeldekortType.DAGP, 2, null))
                .thenReturn(List.of("<xml-1/>", "<xml-2/>"));

        webTestClient
                .get()
                .uri("/api/v1/meldekort/DAGP/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0]").isEqualTo("<xml-1/>")
                .jsonPath("$[1]").isEqualTo("<xml-2/>");

    }

    @Test
    void shouldUseArbeidstimerOverrideWhenProvided() {

        when(onnxService.generateMeldekort(MeldekortType.ATTF, 1, 7.5))
                .thenReturn(List.of("<xml/>"));

        webTestClient
                .get()
                .uri("/api/v1/meldekort/ATTF/1?arbeidstimer=7.5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0]").isEqualTo("<xml/>");

    }

    @Test
    void shouldIgnoreInvalidArbeidstimerValue() {

        when(onnxService.generateMeldekort(MeldekortType.DAGP, 1, null))
                .thenReturn(List.of("<xml/>"));

        webTestClient
                .get()
                .uri("/api/v1/meldekort/DAGP/1?arbeidstimer=ikke-tall")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0]").isEqualTo("<xml/>");

    }

    @Test
    void shouldReturn400WhenMeldegruppeIsInvalid() {

        webTestClient
                .get()
                .uri("/api/v1/meldekort/INVALID/1")
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(onnxService);

    }

    @Test
    void shouldReturn400WhenNumberToGenerateIsNegative() {

        webTestClient
                .get()
                .uri("/api/v1/meldekort/DAGP/-1")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Invalid request");

        verifyNoInteractions(onnxService);

    }

}

