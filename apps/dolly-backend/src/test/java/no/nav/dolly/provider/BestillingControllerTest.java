package no.nav.dolly.provider;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.service.BestillingEventPublisher;
import no.nav.dolly.service.BestillingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BestillingControllerTest {

    private static final Long BESTILLING_ID = 1L;
    private static final Long GRUPPE_ID = 111L;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private BestillingEventPublisher bestillingEventPublisher;

    @InjectMocks
    private BestillingController bestillingController;

    @Test
    void getBestillingById_oppdatererMedPersonstatusOrReturnererBestilling() {

        var bestillingStatus = RsBestillingStatus.builder().build();
        when(bestillingService.fetchBestillingById(any())).thenReturn(Mono.just(new Bestilling()));
        when(mapperFacade.map(any(), any())).thenReturn(bestillingStatus);

        StepVerifier
                .create(bestillingController.getBestillingById(BESTILLING_ID))
                .assertNext(status -> verify(bestillingService).fetchBestillingById(BESTILLING_ID))
                .verifyComplete();
    }

    @Test
    void getBestillingerOk() {

        when(mapperFacade.map(any(), eq(RsBestillingStatus.class)))
                .thenReturn(RsBestillingStatus.builder().id(BESTILLING_ID).build());
        when(bestillingService.getBestillingerFromGruppeIdPaginert(GRUPPE_ID, 0, 10))
                .thenReturn(Flux.just(new Bestilling()));

        StepVerifier.create(bestillingController.getBestillinger(GRUPPE_ID, 0, 10))
                .assertNext(bestilling -> {
                    assertThat(bestilling.getId(), is(equalTo(BESTILLING_ID)));
                    verify(bestillingService).getBestillingerFromGruppeIdPaginert(GRUPPE_ID, 0, 10);
                    verify(mapperFacade).map(any(), eq(RsBestillingStatus.class));
                })
                .verifyComplete();
    }

    @Test
    void stopBestillingProgressOk() {

        when(bestillingService.cancelBestilling(BESTILLING_ID)).thenReturn(Mono.just(new Bestilling()));
        when(mapperFacade.map(any(Bestilling.class), eq(RsBestillingStatus.class)))
                .thenReturn(RsBestillingStatus.builder().id(BESTILLING_ID).build());

        StepVerifier.create(bestillingController.stopBestillingProgress(BESTILLING_ID, null))
                .assertNext(bestilling -> {
                    verify(bestillingService).cancelBestilling(BESTILLING_ID);
                    verify(mapperFacade).map(any(Bestilling.class), eq(RsBestillingStatus.class));
                    assertThat(bestilling.getId(), is(equalTo(BESTILLING_ID)));
                })
                .verifyComplete();
    }

    @Test
    void shouldStreamCompletedBestillingAsSingleEvent() {

        var bestillingEntity = new Bestilling();
        var bestillingStatus = RsBestillingStatus.builder()
                .id(BESTILLING_ID)
                .gruppeId(GRUPPE_ID)
                .ferdig(true)
                .antallLevert(5)
                .antallIdenter(5)
                .build();

        when(bestillingService.fetchBestillingById(BESTILLING_ID)).thenReturn(Mono.just(bestillingEntity));
        when(mapperFacade.map(any(Bestilling.class), eq(RsBestillingStatus.class))).thenReturn(bestillingStatus);

        StepVerifier.create(bestillingController.streamBestillingStatus(BESTILLING_ID))
                .assertNext(sse -> {
                    assertThat(sse.event(), is(equalTo("completed")));
                    assertThat(sse.data().isFerdig(), is(true));
                    assertThat(sse.data().getAntallLevert(), is(5));
                })
                .verifyComplete();
    }

    @Test
    void shouldStreamProgressThenCompleteWhenBestillingFinishes() {

        var bestillingEntity = new Bestilling();
        var progressStatus = RsBestillingStatus.builder()
                .id(BESTILLING_ID)
                .gruppeId(GRUPPE_ID)
                .ferdig(false)
                .antallLevert(2)
                .antallIdenter(5)
                .build();
        var completedStatus = RsBestillingStatus.builder()
                .id(BESTILLING_ID)
                .gruppeId(GRUPPE_ID)
                .ferdig(true)
                .antallLevert(5)
                .antallIdenter(5)
                .build();

        when(bestillingService.fetchBestillingById(BESTILLING_ID)).thenReturn(Mono.just(bestillingEntity));
        when(mapperFacade.map(any(Bestilling.class), eq(RsBestillingStatus.class)))
                .thenReturn(progressStatus)
                .thenReturn(completedStatus);

        when(bestillingEventPublisher.subscribe(BESTILLING_ID))
                .thenReturn(Flux.just(BESTILLING_ID));

        StepVerifier.create(bestillingController.streamBestillingStatus(BESTILLING_ID))
                .assertNext(sse -> {
                    assertThat(sse.event(), is(equalTo("progress")));
                    assertThat(sse.data().getAntallLevert(), is(2));
                })
                .assertNext(sse -> {
                    assertThat(sse.event(), is(equalTo("completed")));
                    assertThat(sse.data().getAntallLevert(), is(5));
                })
                .verifyComplete();
    }

    @Test
    void shouldStreamProgressWithFeil() {

        var bestillingEntity = new Bestilling();
        var bestillingStatus = RsBestillingStatus.builder()
                .id(BESTILLING_ID)
                .gruppeId(GRUPPE_ID)
                .ferdig(true)
                .antallLevert(0)
                .antallIdenter(5)
                .feil("Feil ved opprettelse")
                .build();

        when(bestillingService.fetchBestillingById(BESTILLING_ID)).thenReturn(Mono.just(bestillingEntity));
        when(mapperFacade.map(any(Bestilling.class), eq(RsBestillingStatus.class))).thenReturn(bestillingStatus);

        StepVerifier.create(bestillingController.streamBestillingStatus(BESTILLING_ID))
                .assertNext(sse -> {
                    assertThat(sse.event(), is(equalTo("completed")));
                    assertThat(sse.data().getFeil(), is(equalTo("Feil ved opprettelse")));
                })
                .verifyComplete();
    }
}
