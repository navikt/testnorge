package no.nav.dolly.provider.api;

import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.service.BrukerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("DELETE /api/v1/gruppe")
class TestgruppeControllerDeleteTest extends AbstractControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BrukerService brukerService;

    @MockitoBean
    private PdlDataConsumer pdlDataConsumer;

    @Test
    @DisplayName("Sletter Testgruppe")
    void deleteTestgruppe() {

        var bruker = createBruker().block();
        var testgruppe = createTestgruppe("Testgruppe", bruker).block();

        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(bruker));
        when(pdlDataConsumer.slettPdl(any())).thenReturn(Mono.empty());
        when(brukerService.fetchBrukerOrTeamBruker(any())).thenReturn(Mono.just(bruker));

        webTestClient
                .get()
                .uri("/api/v1/gruppe/{id}", testgruppe.getId())
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .delete()
                .uri("/api/v1/gruppe/{id}", testgruppe.getId())
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("/api/v1/gruppe/{id}", testgruppe.getId())
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}
