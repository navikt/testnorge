package no.nav.dolly.provider.api;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.service.BestillingService;

@RunWith(MockitoJUnitRunner.class)
public class BestillingControllerTest {

    private static final Long BESTILLING_ID = 1L;
    private static final Long GRUPPE_ID = 111L;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private DollyBestillingService dollyBestillingService;

    @InjectMocks
    private BestillingController bestillingController;

    @Test
    public void getBestillingById_oppdatererMedPersonstatusOrReturnererBestilling() {

        RsBestillingStatus bestillingStatus = RsBestillingStatus.builder().build();
        when(bestillingService.fetchBestillingById(any())).thenReturn(new Bestilling());
        when(mapperFacade.map(any(), any())).thenReturn(bestillingStatus);

        RsBestillingStatus res = bestillingController.getBestillingById(BESTILLING_ID);

        assertThat(res, is(bestillingStatus));
    }

    @Test
    public void getBestillingerOk() {
        when(mapperFacade.mapAsList(anyList(), eq(RsBestillingStatus.class)))
                .thenReturn(singletonList(RsBestillingStatus.builder().id(BESTILLING_ID).build()));

        RsBestillingStatus bestilling = bestillingController.getBestillinger(GRUPPE_ID).get(0);

        verify(bestillingService).fetchBestillingerByGruppeId(GRUPPE_ID);
        verify(mapperFacade).mapAsList(anyList(), eq(RsBestillingStatus.class));

        assertThat(bestilling.getId(), is(equalTo(BESTILLING_ID)));
    }

    @Test
    public void stopBestillingProgressOk() {
        when(bestillingService.cancelBestilling(BESTILLING_ID)).thenReturn(Bestilling.builder().build());
        bestillingController.stopBestillingProgress(BESTILLING_ID);

        verify(bestillingService).cancelBestilling(BESTILLING_ID);
        verify(mapperFacade).map(any(Bestilling.class), eq(RsBestillingStatus.class));
    }

    @Test
    public void gjenopprettBestillingOk() {

        when(bestillingService.createBestillingForGjenopprett(eq(BESTILLING_ID), anyList()))
                .thenReturn(Bestilling.builder().build());
        when(mapperFacade.map(any(Bestilling.class), eq(RsBestillingStatus.class)))
                .thenReturn(RsBestillingStatus.builder().id(BESTILLING_ID).build());

        RsBestillingStatus bestilling = bestillingController.gjenopprettBestilling(BESTILLING_ID, null);

        assertThat(bestilling.getId(), is(equalTo(BESTILLING_ID)));
        verify(dollyBestillingService).gjenopprettBestillingAsync(any(Bestilling.class));
        verify(mapperFacade).map(any(Bestilling.class), eq(RsBestillingStatus.class));
    }

    @Test
    public void malBestillingNavnOk() {
        bestillingController.getMalBestillinger();

        verify(bestillingService).fetchMalBestillinger();
        verify(mapperFacade).mapAsList(anyList(), eq(RsBestillingStatus.class));
    }
}