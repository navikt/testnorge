package no.nav.dolly.provider;

import no.nav.dolly.bestilling.tpsmessagingservice.MiljoerConsumer;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAnsettelsesPeriode;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.AarsakTilInnsendingType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.YtelseType;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.service.BrukerService;
import no.nav.testnav.libs.data.pdlforvalter.v1.Identtype;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@DisplayName("POST /api/v1/gruppe")
class TestgruppeControllerPostTest extends AbstractControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private MiljoerConsumer miljoerConsumer;

    @MockitoBean
    private BrukerService brukerService;

    @Test
    @DisplayName("Returnerer opprettet Testgruppe med innlogget bruker som eier")
    void createTestgruppeAndSetCurrentUserAsOwner() {

        var bruker = createBruker().block();
        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(bruker));

        var request = RsOpprettEndreTestgruppe
                .builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build();

        webTestClient
                .post()
                .uri("/api/v1/gruppe")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.navn").isEqualTo("mingruppe")
                .jsonPath("$.hensikt").isEqualTo("hensikt")
                .jsonPath("$.opprettetAv.brukerId").isEqualTo(bruker.getBrukerId())
                .jsonPath("$.sistEndretAv.brukerId").isEqualTo(bruker.getBrukerId());
    }

    @Test
    @DisplayName("Kjører en bestilling gjennom TestGruppeController")
    void createBestilling() {

        var bruker = createBruker().block();
        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(bruker));
        when(miljoerConsumer.getMiljoer())
                .thenReturn(Mono.just(List.of("q1", "q2", "q4", "qx")));

        var testgruppe = createTestgruppe("gruppe", bruker).block();

        var bestilling = RsDollyBestillingRequest
                .builder()
                .environments(Set.of("q1", "q2"))
                .aareg(List.of(
                        RsAareg
                                .builder()
                                .arbeidsforholdstype("ordinaertArbeidsforhold")
                                .ansettelsesPeriode(
                                        RsAnsettelsesPeriode
                                                .builder()
                                                .fom(LocalDateTime.now())
                                                .build())
                                .arbeidsavtale(
                                        RsArbeidsavtale
                                                .builder()
                                                .arbeidstidsordning("ikkeSkift")
                                                .avtaltArbeidstimerPerUke(37.5)
                                                .stillingsprosent(100.0)
                                                .yrke("2521106")
                                                .ansettelsesform("fast")
                                                .build())
                                .arbeidsgiver(
                                        RsOrganisasjon
                                                .builder()
                                                .aktoertype("ORG")
                                                .orgnummer("896929119")
                                                .build())
                                .build()))
                .pdldata(
                        PdlPersondata
                                .builder()
                                .opprettNyPerson(
                                        PdlPersondata.PdlPerson
                                                .builder()
                                                .identtype(Identtype.FNR)
                                                .build())
                                .build())
                .inntektsmelding(
                        RsInntektsmelding
                                .builder()
                                .inntekter(List.of(
                                        RsInntektsmelding.Inntektsmelding
                                                .builder()
                                                .aarsakTilInnsending(AarsakTilInnsendingType.NY)
                                                .arbeidsforhold(
                                                        RsInntektsmelding.RsArbeidsforhold
                                                                .builder()
                                                                .beregnetInntekt(
                                                                        RsInntektsmelding.RsAltinnInntekt
                                                                                .builder()
                                                                                .beloep(1.0)
                                                                                .build())
                                                                .build())
                                                .arbeidsgiver(
                                                        RsInntektsmelding.RsArbeidsgiver
                                                                .builder()
                                                                .virksomhetsnummer("896929119")
                                                                .build())
                                                .avsendersystem(
                                                        RsInntektsmelding.RsAvsendersystem
                                                                .builder()
                                                                .innsendingstidspunkt(LocalDateTime.now())
                                                                .build())
                                                .ytelse(YtelseType.PLEIEPENGER_BARN)
                                                .build()))
                                .joarkMetadata(
                                        RsInntektsmelding.JoarkMetadata
                                                .builder()
                                                .tema("OMS")
                                                .build())
                                .build())
                .build();

        webTestClient
                .post()
                .uri("/api/v1/gruppe/{gruppeId}/bestilling", testgruppe.getId())
                .contentType(APPLICATION_JSON)
                .bodyValue(bestilling)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.environments").isNotEmpty()
                .jsonPath("$.environments").value(containsInAnyOrder("q1", "q2"))
                .jsonPath("$.gruppeId").isEqualTo(testgruppe.getId());
    }
}
