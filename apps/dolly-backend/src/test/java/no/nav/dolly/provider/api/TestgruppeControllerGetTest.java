package no.nav.dolly.provider.api;

import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppePage;
import no.nav.dolly.service.BrukerService;
import no.nav.testnav.libs.dto.dokarkiv.v1.Bruker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Random;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("GET /api/v1/gruppe")
class TestgruppeControllerGetTest extends AbstractControllerTest {

    private static final Bruker BRUKER = Bruker.builder()
            .brukerId("test")
            .build();
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BrukerService brukerService;

    @BeforeEach
    void setup() {
        when(brukerService.fetchOrCreateBruker()).thenReturn(BRUKER);
        when(brukerService.fetchBrukerOrTeamBruker(any())).thenReturn(BRUKER);
    }

    @Test
    @DisplayName("Returnerer testgrupper tilknyttet til bruker-ID gjennom favoritter")
    void shouldGetTestgrupperWithNavIdent() {

        var bruker = createBruker().block();
        createTestgruppe("Gruppen er ikke en favoritt", bruker).block();
        createTestgruppe("Gruppen er en favoritt", bruker).block();
        var bruker = super.createBruker();
        when(brukerService.fetchOrCreateBruker(any())).thenReturn(bruker);
        when(brukerService.fetchBrukerOrTeamBruker(any())).thenReturn(bruker);

        when(brukerService.fetchOrCreateBruker(bruker.getBrukerId()))
                .thenReturn(Mono.just(bruker));
        var testgruppe2 = super.createTestgruppe("Gruppen er en favoritt", bruker);
        bruker.setFavoritter(Set.of(testgruppe2));
        bruker = super.saveBruker(bruker);

        webTestClient
                .get()
                .uri("/api/v1/gruppe?brukerId={brukerId}", bruker.getBrukerId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(RsTestgruppePage.class)
                .value(rsTestgruppePage -> {
                    assertThat(rsTestgruppePage.getAntallElementer(), is(2L));
                    assertThat(rsTestgruppePage.getContents().size(), is(2));
                    assertThat(rsTestgruppePage.getContents().getFirst().getNavn(), is("Gruppen er ikke en favoritt"));
                    assertThat(rsTestgruppePage.getContents().getLast().getNavn(), is("Gruppen er en favoritt"));
                });
    }

    @Test
    @DisplayName("Returnerer HTTP 404 med korrekt feilmelding i body")
    void shouldFail404NotFound() {

        var id = new Random().nextLong();
        var bruker = createBruker().block();
        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(bruker));

        webTestClient
                .get().uri("/api/v1/gruppe/{gruppeId}", id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(reultat -> assertThat(reultat,
                        containsString("Gruppe med id " + id + " ble ikke funnet.")));
    }

    @Test
    @DisplayName("Returnerer Testgruppe")
    void shouldReturnTestgruppe() {

        var bruker = createBruker().block();
        var testgruppe = createTestgruppe("Testgruppe", bruker).block();
        for (int i = 1; i < 11; i++) {
            createTestident("Ident " + i, testgruppe).block();
        }

        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(bruker));

        webTestClient
                .get()
                .uri("/api/v1/gruppe/{gruppeId}", testgruppe.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(RsTestgruppeMedBestillingId.class)
                .value(rsTestgruppe -> {
                    assertThat(rsTestgruppe.getNavn(), is(testgruppe.getNavn()));
                    assertThat(rsTestgruppe.getHensikt(), is(testgruppe.getHensikt()));
                    assertThat(rsTestgruppe.getAntallIdenter(), is(10));
                    assertThat(rsTestgruppe.getId(), is(testgruppe.getId()));
                });
    }
}

