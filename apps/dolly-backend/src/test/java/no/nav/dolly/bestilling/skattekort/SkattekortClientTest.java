package no.nav.dolly.bestilling.skattekort;

import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.skattekort.domain.ArbeidstakerSkatt;
import no.nav.dolly.bestilling.skattekort.domain.Forskuddstrekk;
import no.nav.dolly.bestilling.skattekort.domain.Frikort;
import no.nav.dolly.bestilling.skattekort.domain.Skattekort;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortDTO;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortHentRequest;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortRequest;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortResponse;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortV2DTO;
import no.nav.dolly.bestilling.skattekort.domain.Skattekortmelding;
import no.nav.dolly.bestilling.skattekort.domain.Tilleggsopplysning;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.skattekort.SkattekortRequestDTO;
import no.nav.dolly.service.TransactionHelperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static no.nav.dolly.bestilling.skattekort.domain.Resultatstatus.IKKE_SKATTEKORT;
import static no.nav.dolly.bestilling.skattekort.domain.Tilleggsopplysning.KILDESKATT_PAA_LOENN;
import static no.nav.dolly.bestilling.skattekort.domain.Tilleggsopplysning.OPPHOLD_I_TILTAKSSONE;
import static no.nav.dolly.bestilling.skattekort.domain.Trekkode.LOENN_FRA_HOVEDARBEIDSGIVER;
import static no.nav.dolly.bestilling.skattekort.domain.Trekkode.LOENN_FRA_NAV;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkattekortClientTest {

    private static final String IDENT = "12345678910";

    @Mock
    private TransactionHelperService transactionHelperService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private SkattekortConsumer skattekortConsumer;

    @InjectMocks
    private SkattekortClient skattekortClient;

    @Test
    void shouldValidateSkattekortMedTilleggsopplysningOK() {

        val dollyPerson = DollyPerson.builder().ident(IDENT).build();
        val argumentCaptor = ArgumentCaptor.forClass(String.class);
        val bestilling = RsDollyUtvidetBestilling.builder()
                .environments(Set.of("q2"))
                .skattekort(SkattekortRequestDTO.builder()
                        .arbeidsgiverSkatt(List.of(ArbeidstakerSkatt.builder()
                                .arbeidstaker(List.of(Skattekortmelding.builder()
                                        .inntektsaar(2025)
                                        .tilleggsopplysning(List.of(Tilleggsopplysning.OPPHOLD_PAA_SVALBARD))
                                        .build()))
                                .build()))
                        .build())
                .build();

        when(mapperFacade.map(any(), eq(SkattekortRequest.class), any())).thenReturn(SkattekortRequest.builder()
                .skattekort(SkattekortDTO.builder()
                        .inntektsaar(2025)
                        .tilleggsopplysningList(List.of(OPPHOLD_I_TILTAKSSONE.getDescription()))
                        .build())
                .build());
        when(transactionHelperService.persister(any(), any(), any(), argumentCaptor.capture()))
                .thenReturn(Mono.just(new BestillingProgress()));
        when(skattekortConsumer.sendSkattekort(any(), anyString())).thenReturn(Mono.just(SkattekortResponse.builder()
                .status(HttpStatus.CREATED)
                .build()));

        StepVerifier.create(
                        skattekortClient.gjenopprett(bestilling, dollyPerson, new BestillingProgress(), true))
                .assertNext(_ -> {
                    verify(mapperFacade).map(any(), eq(SkattekortRequest.class), any());
                    verify(skattekortConsumer).sendSkattekort(any(), eq("q2"));
                    verify(transactionHelperService, times(2)).persister(any(), any(), any(), anyString());
                    assertThat(argumentCaptor.getAllValues(), hasItems("q2:Info= Oppretting startet mot SKATTEKORT ...", "q2:OK"));
                })
                .verifyComplete();
    }

    @Test
    void shouldFailSkattekortMedTilleggsopplysningOK() {

        val dollyPerson = DollyPerson.builder().ident(IDENT).build();
        val bestilling = RsDollyUtvidetBestilling.builder()
                .environments(Set.of("q1"))
                .skattekort(SkattekortRequestDTO.builder()
                        .arbeidsgiverSkatt(List.of(ArbeidstakerSkatt.builder()
                                .arbeidstaker(List.of(Skattekortmelding.builder()
                                        .tilleggsopplysning(List.of(KILDESKATT_PAA_LOENN))
                                        .build()))
                                .build()))
                        .build())
                .build();
        val argumentCaptor = ArgumentCaptor.forClass(String.class);

        when(transactionHelperService.persister(any(), any(), any(), argumentCaptor.capture()))
                .thenReturn(Mono.just(new BestillingProgress()));

        StepVerifier.create(
                        skattekortClient.gjenopprett(bestilling, dollyPerson, new BestillingProgress(), true))
                .assertNext(_ -> {
                    verify(transactionHelperService).persister(any(), any(), any(), anyString());
                    assertThat(argumentCaptor.getAllValues(), hasItems(is(equalTo("q1:Avvik: Validering feilet: Trekkode er ikke gyldig"))));
                })
                .verifyComplete();
    }

    @Test
    void shouldValidateSkattekortMedTrekkodeOK() {

        val dollyPerson = DollyPerson.builder().ident(IDENT).build();
        val argumentCaptor = ArgumentCaptor.forClass(String.class);

        val bestilling = RsDollyUtvidetBestilling.builder()
                .environments(Set.of("q1"))
                .skattekort(SkattekortRequestDTO.builder()
                        .arbeidsgiverSkatt(List.of(ArbeidstakerSkatt.builder()
                                .arbeidstaker(List.of(Skattekortmelding.builder()
                                        .inntektsaar(2026)
                                        .skattekort(Skattekort.builder()
                                                .forskuddstrekk(List.of(Forskuddstrekk.builder()
                                                        .frikort(Frikort.builder()
                                                                .trekkode(LOENN_FRA_NAV)
                                                                .frikortbeloep(50000)
                                                                .build())
                                                        .build()))
                                                .build())
                                        .build()))
                                .build()))
                        .build())
                .build();

        when(mapperFacade.map(any(), eq(SkattekortRequest.class), any())).thenReturn(SkattekortRequest.builder()
                .skattekort(SkattekortDTO.builder()
                        .inntektsaar(2026)
                        .forskuddstrekkList(List.of(SkattekortDTO.ForskuddstrekkDTO.builder()
                                .trekkode(LOENN_FRA_NAV.getDescription())
                                .frikort(SkattekortDTO.FrikortDTO.builder()
                                        .frikortBeloep(50000)
                                        .build())
                                .build()))
                        .build())
                .build());

        when(transactionHelperService.persister(any(), any(), any(), argumentCaptor.capture()))
                .thenReturn(Mono.just(new BestillingProgress()));
        when(skattekortConsumer.sendSkattekort(any(), anyString())).thenReturn(Mono.just(SkattekortResponse.builder()
                .status(HttpStatus.CREATED)
                .build()));

        StepVerifier.create(
                        skattekortClient.gjenopprett(bestilling, dollyPerson, new BestillingProgress(), true))
                .assertNext(_ -> {
                    verify(mapperFacade).map(any(), eq(SkattekortRequest.class), any());
                    verify(skattekortConsumer).sendSkattekort(any(), eq("q1"));
                    verify(transactionHelperService, times(2)).persister(any(), any(), any(), anyString());
                    assertThat(argumentCaptor.getAllValues(), hasItems("q1:Info= Oppretting startet mot SKATTEKORT ...", "q1:OK"));
                })
                .verifyComplete();
    }

    @Test
    void shouldFailSkattekortMedUgyldigTrekkodeOK() {

        val dollyPerson = DollyPerson.builder().ident(IDENT).build();
        val bestilling = RsDollyUtvidetBestilling.builder()
                .environments(Set.of("q2"))
                .skattekort(SkattekortRequestDTO.builder()
                        .arbeidsgiverSkatt(List.of(ArbeidstakerSkatt.builder()
                                .arbeidstaker(List.of(Skattekortmelding.builder()
                                        .skattekort(Skattekort.builder()
                                                .forskuddstrekk(List.of(Forskuddstrekk.builder()
                                                        .frikort(Frikort.builder()
                                                                .trekkode(LOENN_FRA_HOVEDARBEIDSGIVER)
                                                                .frikortbeloep(50000)
                                                                .build())
                                                        .build()))
                                                .build())
                                        .build()))
                                .build()))
                        .build())
                .build();
        val argumentCaptor = ArgumentCaptor.forClass(String.class);

        when(transactionHelperService.persister(any(), any(), any(), argumentCaptor.capture()))
                .thenReturn(Mono.just(new BestillingProgress()));

        StepVerifier.create(
                        skattekortClient.gjenopprett(bestilling, dollyPerson, new BestillingProgress(), true))
                .assertNext(_ -> {
                    verify(transactionHelperService).persister(any(), any(), any(), anyString());
                    assertThat(argumentCaptor.getAllValues(), hasItems(is(equalTo("q2:Avvik: Validering feilet: Trekkode er ikke gyldig"))));
                })
                .verifyComplete();
    }

    @Test
    void shouldReleaseSkattekortForEachIdentAndYearInSupportedEnvironments() {

        val identer = List.of(IDENT, "10987654321");
        val response = SkattekortResponse.builder()
                .status(HttpStatus.OK)
                .skattekort(SkattekortV2DTO.builder()
                        .data(List.of(
                                SkattekortV2DTO.SkattekortResponseDTO.builder()
                                        .inntektsaar(2025)
                                        .resultatForSkattekort("skattekortopplysningerOK")
                                        .tilleggsopplysningList(List.of(OPPHOLD_I_TILTAKSSONE.getDescription()))
                                        .build(),
                                SkattekortV2DTO.SkattekortResponseDTO.builder()
                                        .inntektsaar(2026)
                                        .resultatForSkattekort("skattekortopplysningerOK")
                                        .build()))
                        .build())
                .build();

        when(skattekortConsumer.hentSkattekort(any(), anyString())).thenReturn(Mono.just(response));
        when(skattekortConsumer.sendSkattekort(any(), anyString())).thenReturn(Mono.just(
                SkattekortResponse.builder().status(HttpStatus.CREATED).build()));

        skattekortClient.release(identer);

        for (val miljoe : List.of("q1", "q2")) {
            val requestCaptor = ArgumentCaptor.forClass(SkattekortRequest.class);
            verify(skattekortConsumer, times(4)).sendSkattekort(requestCaptor.capture(), eq(miljoe));
            assertThat(requestCaptor.getAllValues(), containsInAnyOrder(
                    releaseRequest(IDENT, 2025),
                    releaseRequest(IDENT, 2026),
                    releaseRequest("10987654321", 2025),
                    releaseRequest("10987654321", 2026)));
            for (val ident : identer) {
                verify(skattekortConsumer).hentSkattekort(
                        SkattekortHentRequest.builder().fnr(ident).build(), miljoe);
            }
        }
        verifyNoMoreInteractions(skattekortConsumer);
        verifyNoInteractions(mapperFacade, transactionHelperService);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotSendSkattekortOnReleaseWhenResponseHasNoData(
            List<SkattekortV2DTO.SkattekortResponseDTO> data) {

        when(skattekortConsumer.hentSkattekort(any(), anyString())).thenReturn(Mono.just(
                SkattekortResponse.builder()
                        .status(HttpStatus.OK)
                        .skattekort(SkattekortV2DTO.builder().data(data).build())
                        .build()));

        skattekortClient.release(List.of(IDENT));

        for (val miljoe : List.of("q1", "q2")) {
            verify(skattekortConsumer).hentSkattekort(
                    SkattekortHentRequest.builder().fnr(IDENT).build(), miljoe);
        }
        verify(skattekortConsumer, never()).sendSkattekort(any(), anyString());
        verifyNoInteractions(mapperFacade, transactionHelperService);
    }

    private static SkattekortRequest releaseRequest(String ident, int inntektsaar) {

        return SkattekortRequest.builder()
                .fnr(ident)
                .skattekort(SkattekortDTO.builder()
                        .inntektsaar(inntektsaar)
                        .resultatForSkattekort(IKKE_SKATTEKORT.getDescription())
                        .build())
                .build();
    }
}