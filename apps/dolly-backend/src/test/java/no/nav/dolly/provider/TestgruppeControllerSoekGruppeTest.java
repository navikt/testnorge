package no.nav.dolly.provider;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.projection.RsGruppeFragment;
import no.nav.dolly.service.BrukerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("GET /api/v1/gruppe/soekGruppe")
class TestgruppeControllerSoekGruppeTest extends AbstractControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BrukerService brukerService;

    private Bruker bruker;

    @BeforeEach
    void setup() {
        bruker = createBruker().block();
        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(bruker));
        when(brukerService.fetchOrCreateBruker(any())).thenReturn(Mono.just(bruker));
    }

    @Test
    @DisplayName("Returnerer grupper som matcher på navn")
    void shouldReturnGrupperMatchingByNavn() {

        createTestgruppe("SoekTestUnikNavn Alpha", bruker).block();
        createTestgruppe("SoekTestUnikNavn Beta", bruker).block();

        webTestClient
                .get()
                .uri("/api/v1/gruppe/soekGruppe?fragment=SoekTestUnikNavn")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RsGruppeFragment.class)
                .value(resultat -> {
                    assertThat(resultat.size(), is(2));
                    assertThat(resultat.get(0).getNavn(), is("SoekTestUnikNavn Alpha"));
                    assertThat(resultat.get(1).getNavn(), is("SoekTestUnikNavn Beta"));
                });
    }

    @Test
    @DisplayName("Returnerer grupper som matcher på ID")
    void shouldReturnGrupperMatchingById() {

        var testgruppe = createTestgruppe("Min testgruppe", bruker).block();

        webTestClient
                .get()
                .uri("/api/v1/gruppe/soekGruppe?fragment={id}", testgruppe.getId().toString())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RsGruppeFragment.class)
                .value(resultat -> {
                    assertThat(resultat.size(), is(1));
                    assertThat(resultat.getFirst().getId(), is(testgruppe.getId()));
                    assertThat(resultat.getFirst().getNavn(), is("Min testgruppe"));
                });
    }

    @Test
    @DisplayName("Returnerer tom liste når ingen grupper matcher")
    void shouldReturnEmptyListWhenNoMatch() {

        createTestgruppe("EksisterendeGruppeXyz", bruker).block();

        webTestClient
                .get()
                .uri("/api/v1/gruppe/soekGruppe?fragment=FantasiNavnSomIkkeFinnes999")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RsGruppeFragment.class)
                .value(resultat -> assertThat(resultat.size(), is(0)));
    }

    @Test
    @DisplayName("Søk er case-insensitive")
    void shouldBeCaseInsensitive() {

        createTestgruppe("CaseTestXyzUnikt", bruker).block();

        webTestClient
                .get()
                .uri("/api/v1/gruppe/soekGruppe?fragment=CASETESTXYZUNIKT")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RsGruppeFragment.class)
                .value(resultat -> {
                    assertThat(resultat.size(), is(1));
                    assertThat(resultat.getFirst().getNavn(), is("CaseTestXyzUnikt"));
                });
    }

    @Test
    @DisplayName("Returnerer kun delvis treff på navn")
    void shouldReturnPartialNameMatch() {

        createTestgruppe("PartialTestXyz for skattetesting", bruker).block();
        createTestgruppe("PartialTestXyz for pensjon", bruker).block();

        webTestClient
                .get()
                .uri("/api/v1/gruppe/soekGruppe?fragment=PartialTestXyz for skatte")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RsGruppeFragment.class)
                .value(resultat -> {
                    assertThat(resultat.size(), is(1));
                    assertThat(resultat.getFirst().getNavn(), is("PartialTestXyz for skattetesting"));
                });
    }

    @Test
    @DisplayName("Returnerer resultater sortert etter ID")
    void shouldReturnResultsSortedById() {

        var gruppe1 = createTestgruppe("SortTestUnik Alpha", bruker).block();
        var gruppe2 = createTestgruppe("SortTestUnik Beta", bruker).block();
        var gruppe3 = createTestgruppe("SortTestUnik Gamma", bruker).block();

        webTestClient
                .get()
                .uri("/api/v1/gruppe/soekGruppe?fragment=SortTestUnik")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RsGruppeFragment.class)
                .value(resultat -> {
                    assertThat(resultat.size(), is(3));
                    assertThat(resultat.get(0).getId(), is(gruppe1.getId()));
                    assertThat(resultat.get(1).getId(), is(gruppe2.getId()));
                    assertThat(resultat.get(2).getId(), is(gruppe3.getId()));
                });
    }

    @Test
    @DisplayName("Returnerer grupper som matcher på både ID og navn")
    void shouldReturnGrupperMatchingByBothIdAndNavn() {

        var matchGruppe = createTestgruppe("KombiTestUnik BeggeMatch", bruker).block();
        createTestgruppe("KombiTestUnik AnnenGruppe", bruker).block();

        webTestClient
                .get()
                .uri("/api/v1/gruppe/soekGruppe?fragment={fragment}",
                        matchGruppe.getId() + " BeggeMatch")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RsGruppeFragment.class)
                .value(resultat -> {
                    assertThat(resultat.size(), is(1));
                    assertThat(resultat.getFirst().getId(), is(matchGruppe.getId()));
                    assertThat(resultat.getFirst().getNavn(), is("KombiTestUnik BeggeMatch"));
                });
    }

    @Test
    @DisplayName("Returnerer tomt resultat ved kun mellomrom som fragment")
    void shouldReturnEmptyForBlankFragment() {

        createTestgruppe("BlankTestUnik Gruppe", bruker).block();

        webTestClient
                .get()
                .uri("/api/v1/gruppe/soekGruppe?fragment={fragment}", "   ")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RsGruppeFragment.class)
                .value(resultat -> assertThat(resultat.size(), is(0)));
    }

    @Test
    @DisplayName("Returnerer kun grupper der alle ord i søk matcher på navn")
    void shouldMatchAllWordsInMultiWordSearch() {

        createTestgruppe("FlereordUnik for skattetesting", bruker).block();
        createTestgruppe("FlereordUnik for pensjonstesting", bruker).block();

        webTestClient
                .get()
                .uri("/api/v1/gruppe/soekGruppe?fragment=FlereordUnik skatte")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RsGruppeFragment.class)
                .value(resultat -> {
                    assertThat(resultat.size(), is(1));
                    assertThat(resultat.getFirst().getNavn(), is("FlereordUnik for skattetesting"));
                });
    }
}
