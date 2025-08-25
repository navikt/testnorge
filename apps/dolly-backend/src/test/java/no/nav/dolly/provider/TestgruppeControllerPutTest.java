package no.nav.dolly.provider;

import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.dolly.service.BrukerService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;


@DisplayName("PUT /api/v1/gruppe")
class TestgruppeControllerPutTest extends AbstractControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BrukerService brukerService;

    @Test
    @DisplayName("Returnerer HTTP 404 med korrekt feilmelding hvis Testgruppe ikke finnes")
    void shouldFail404WhenTestgruppeDontExist() {

        var request = RsOpprettEndreTestgruppe
                .builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build();
        var id = new Random().nextLong();

        when(brukerService.fetchOrCreateBruker()).thenReturn(createBruker());

        webTestClient
                .put()
                .uri("/api/v1/gruppe/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .value(CoreMatchers.containsString("Gruppe med id " + id + " ble ikke funnet."));
    }

    @Test
    @DisplayName("Oppdaterer informasjon om Testgruppe")
    void updateTestgruppe() {

        var bruker = createBruker().block();
        var testgruppe = createTestgruppe("Opprinnelig gruppe", bruker).block();
        var request = RsOpprettEndreTestgruppe
                .builder()
                .navn("Endret gruppe")
                .hensikt("Endret hensikt")
                .build();

        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(bruker));

        webTestClient

                .put()
                .uri("/api/v1/gruppe/{id}", testgruppe.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()                .isOk()
                .expectBody(RsTestgruppeMedBestillingId.class)
                .value(response -> {
                    assertThat(response.getId()).isEqualTo(testgruppe.getId());
                    assertThat(response.getNavn()).isEqualTo("Endret gruppe");
                    assertThat(response.getHensikt()).isEqualTo("Endret hensikt");
                });
    }
}
