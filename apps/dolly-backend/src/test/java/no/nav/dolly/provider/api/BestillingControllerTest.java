package no.nav.dolly.provider.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.provider.BestillingController;
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
}
