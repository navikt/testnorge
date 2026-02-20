package no.nav.pdl.forvalter.controller;

import no.nav.pdl.forvalter.service.EksistensService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AvailibilityResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(EksistensController.class)
class EksistensControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private EksistensService eksistensService;

    @Test
    void shouldCheckAvailabilityReactively() {

        when(eksistensService.checkAvailibility(any()))
                .thenReturn(Flux.just(
                        new AvailibilityResponseDTO("12034567849", "Gyldig og ledig", true)
                ));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/eksistens")
                        .queryParam("identer", "12034567849")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].ident").isEqualTo("12034567849")
                .jsonPath("$[0].available").isEqualTo(true);
    }

    @Test
    void shouldReturnUnavailableForExistingIdent() {

        when(eksistensService.checkAvailibility(any()))
                .thenReturn(Flux.just(
                        new AvailibilityResponseDTO("12034567849", "Ikke ledig -- ident finnes allerede i database", false)
                ));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/eksistens")
                        .queryParam("identer", "12034567849")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].ident").isEqualTo("12034567849")
                .jsonPath("$[0].available").isEqualTo(false);
    }

}
