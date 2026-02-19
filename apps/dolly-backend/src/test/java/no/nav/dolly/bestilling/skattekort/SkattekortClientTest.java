package no.nav.dolly.bestilling.skattekort;

import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.skattekort.domain.ArbeidstakerSkatt;
import no.nav.dolly.bestilling.skattekort.domain.Forskuddstrekk;
import no.nav.dolly.bestilling.skattekort.domain.Frikort;
import no.nav.dolly.bestilling.skattekort.domain.Skattekort;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortDTO;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortRequest;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortResponse;
import no.nav.dolly.bestilling.skattekort.domain.Skattekortmelding;
import no.nav.dolly.bestilling.skattekort.domain.Tilleggsopplysning;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.skattekort.SkattekortRequestDTO;
import no.nav.dolly.service.TransactionHelperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static no.nav.dolly.bestilling.skattekort.domain.Tilleggsopplysning.KILDESKATT_PAA_LOENN;
import static no.nav.dolly.bestilling.skattekort.domain.Tilleggsopplysning.OPPHOLD_I_TILTAKSSONE;
import static no.nav.dolly.bestilling.skattekort.domain.Trekkode.LOENN_FRA_HOVEDARBEIDSGIVER;
import static no.nav.dolly.bestilling.skattekort.domain.Trekkode.LOENN_FRA_NAV;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
        when(skattekortConsumer.sendSkattekort(any())).thenReturn(Mono.just(SkattekortResponse.builder()
                .status(HttpStatus.CREATED)
                .build()));

        StepVerifier.create(
                        skattekortClient.gjenopprett(bestilling, dollyPerson, new BestillingProgress(), true))
                .assertNext(progress -> {
                    verify(mapperFacade).map(any(), eq(SkattekortRequest.class), any());
                    verify(skattekortConsumer).sendSkattekort(any());
                    verify(transactionHelperService, times(2)).persister(any(), any(), any(), anyString());
                    assertThat(argumentCaptor.getAllValues(), hasItems("Info= Oppretting startet mot SKATTEKORT ...", "2025|Skattekort lagret"));
                })
                .verifyComplete();
    }

    @Test
    void shouldFailSkattekortMedTilleggsopplysningOK() {

        val dollyPerson = DollyPerson.builder().ident(IDENT).build();
        val bestilling = RsDollyUtvidetBestilling.builder()
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
                .assertNext(progress -> {
                    verify(transactionHelperService).persister(any(), any(), any(), anyString());
                    assertThat(argumentCaptor.getAllValues(), hasItems(is(equalTo("Avvik: Validering feilet: Trekkode er ikke gyldig"))));
                })
                .verifyComplete();
    }

    @Test
    void shouldValidateSkattekortMedTrekkodeOK() {

        val dollyPerson = DollyPerson.builder().ident(IDENT).build();
        val argumentCaptor = ArgumentCaptor.forClass(String.class);

        val bestilling = RsDollyUtvidetBestilling.builder()
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
        when(skattekortConsumer.sendSkattekort(any())).thenReturn(Mono.just(SkattekortResponse.builder()
                .status(HttpStatus.CREATED)
                .build()));

        StepVerifier.create(
                        skattekortClient.gjenopprett(bestilling, dollyPerson, new BestillingProgress(), true))
                .assertNext(progress -> {
                    verify(mapperFacade).map(any(), eq(SkattekortRequest.class), any());
                    verify(skattekortConsumer).sendSkattekort(any());
                    verify(transactionHelperService, times(2)).persister(any(), any(), any(), anyString());
                    assertThat(argumentCaptor.getAllValues(), hasItems("Info= Oppretting startet mot SKATTEKORT ...", "2026|Skattekort lagret"));
                })
                .verifyComplete();
    }

    @Test
    void shouldFailSkattekortMedUgyldigTrekkodeOK() {

        val dollyPerson = DollyPerson.builder().ident(IDENT).build();
        val bestilling = RsDollyUtvidetBestilling.builder()
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
                .assertNext(progress -> {
                    verify(transactionHelperService).persister(any(), any(), any(), anyString());
                    assertThat(argumentCaptor.getAllValues(), hasItems(is(equalTo("Avvik: Validering feilet: Trekkode er ikke gyldig"))));
                })
                .verifyComplete();
    }
}