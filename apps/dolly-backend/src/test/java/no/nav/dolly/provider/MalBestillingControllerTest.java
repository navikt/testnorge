package no.nav.dolly.provider;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.repository.BestillingMalRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.service.BrukerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static java.time.LocalDateTime.now;
import static org.mockito.Mockito.when;

class MalBestillingControllerTest extends AbstractControllerTest {

    private static final String MALNAVN_EN = "testmalEn";
    private static final String MALNAVN_TO = "testmalTo";
    private static final String NYTT_MALNAVN = "nyttMalnavn";
    private static final String BEST_KRITERIER = "{\"test\":true}";
    private static final Bruker DUMMY_EN = Bruker.builder()
            .brukerId("testbruker_en")
            .brukernavn("test_en")
            .brukertype(Bruker.Brukertype.AZURE)
            .epost("epost@test_en")
            .build();
    private static final Bruker DUMMY_TO = Bruker.builder()
            .brukerId("testbruker_to")
            .brukernavn("test_to")
            .brukertype(Bruker.Brukertype.AZURE)
            .epost("epost@test_to")
            .build();
    private static final String IDENT = "12345678912";
    private static final String BESKRIVELSE = "Teste";
    private static final String TESTGRUPPE = "Testgruppe";
    private static final String UGYLDIG_BESTILLINGID = "999";

    @Autowired
    private BestillingRepository bestillingRepository;

    @MockitoBean
    private BrukerService brukerService;

    @Autowired
    private BestillingMalRepository bestillingMalRepository;

    @Autowired
    private WebTestClient webTestClient;

    @AfterEach
    void tearDown() {

        bestillingMalRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Oppretter og returnerer alle maler tilknyttet to forskjellige brukere")
    void shouldCreateAndGetMaler() {

        var brukerEn = saveBruker(DUMMY_EN).block();
        var brukerTo = saveBruker(DUMMY_TO).block();
        var dummyGruppe = createTestgruppe(TESTGRUPPE, brukerEn).block();

        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(brukerEn));

        var bestilling = saveDummyBestilling(brukerEn, dummyGruppe).block();
        var bestillingTo = saveDummyBestilling(brukerTo, dummyGruppe).block();

        webTestClient
                .post()
                .uri("/api/v1/malbestilling?bestillingId={bestillingId}&malNavn={malNavn}",
                        bestilling.getId(), MALNAVN_EN)
                .exchange()
                .expectStatus()
                .isOk();

        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(brukerTo));

        webTestClient
                .post()
                .uri("/api/v1/malbestilling?bestillingId={bestillingId}&malNavn={malNavn}",
                        bestillingTo.getId(), MALNAVN_TO)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("/api/v1/malbestilling/oversikt")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.brukereMedMaler.length()").isEqualTo(4)
                .jsonPath("$.brukereMedMaler[2].brukernavn").isEqualTo("test_en")
                .jsonPath("$.brukereMedMaler[2].brukerId").isEqualTo("testbruker_en")
                .jsonPath("$.brukereMedMaler[3].brukernavn").isEqualTo("test_to")
                .jsonPath("$.brukereMedMaler[3].brukerId").isEqualTo("testbruker_to");
    }

    @Test
    @DisplayName("Oppretter mal fra gjeldende bestilling og tester at NotFoundError blir kastet ved ugyldig bestillingId")
    void shouldCreateMalerFromExistingOrder() {

        var brukerEn = saveBruker(DUMMY_EN).block();
        var dummyGruppe = createTestgruppe(TESTGRUPPE, brukerEn).block();
        var bestilling = saveDummyBestilling(brukerEn, dummyGruppe).block();

        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(brukerEn));

        webTestClient
                .post()
                .uri("/api/v1/malbestilling?bestillingId={bestillingId}&malNavn={malNavn}",
                        bestilling.getId(), MALNAVN_EN)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .post()
                .uri("/api/v1/malbestilling?bestillingId={bestillingId}&malNavn={malNavn}",
                        UGYLDIG_BESTILLINGID, MALNAVN_TO)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Mal med id " + UGYLDIG_BESTILLINGID + " finnes ikke");
    }

    @Test
    @DisplayName("Oppretter, endrer navn på og sletter til slutt bestillingMal")
    void shouldCreateUpdateAndDeleteMal() {

        var brukerEn = saveBruker(DUMMY_EN).block();
        var bestillingMal = saveDummyBestillingMal(brukerEn).block();

        webTestClient
                .put()
                .uri("/api/v1/malbestilling/id/{id}?malNavn={malNavn}",
                        bestillingMal.getId(), NYTT_MALNAVN)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.get()
                .uri("/api/v1/malbestilling/brukerId/{brukerId}", brukerEn.getBrukerId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$[0].malNavn").isEqualTo(NYTT_MALNAVN);

        webTestClient
                .delete()
                .uri("/api/v1/malbestilling/id/{id}", bestillingMal.getId())
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.get()
                .uri("/api/v1/malbestilling/brukerId/{brukerId}", brukerEn.getBrukerId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$").value(Matchers.hasSize(0));
    }

    Mono<BestillingMal> saveDummyBestillingMal(Bruker bruker) {

        return bestillingMalRepository.save(
                BestillingMal
                        .builder()
                        .bestKriterier(BEST_KRITERIER)
                        .brukerId(bruker.getId())
                        .malNavn(MALNAVN_EN)
                        .sistOppdatert(now())
                        .build()
        );
    }

    Mono<Bestilling> saveDummyBestilling(Bruker bruker, Testgruppe testgruppe) {

        return bestillingRepository.save(
                Bestilling
                        .builder()
                        .gruppeId(testgruppe.getId())
                        .ferdig(false)
                        .antallIdenter(1)
                        .bestKriterier(BEST_KRITERIER)
                        .bruker(bruker)
                        .beskrivelse(BESKRIVELSE)
                        .sistOppdatert(now())
                        .ident(IDENT)
                        .navSyntetiskIdent(true)
                        .build()
        );
    }
}