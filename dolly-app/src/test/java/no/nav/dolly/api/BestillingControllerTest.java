package no.nav.dolly.api;

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
import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.domain.resultset.RsBestillingStatus;
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
    public void checkBestillingsstatus_oppdatererMedPersonstatusOrReturnererBestilling() {

        RsBestilling rsBestilling = new RsBestilling();
        when(bestillingService.fetchBestillingById(any())).thenReturn(new Bestilling());
        when(mapperFacade.map(any(), any())).thenReturn(rsBestilling);

        RsBestillingStatus res = bestillingController.checkBestillingsstatus(BESTILLING_ID);

        assertThat(res, is(rsBestilling));
    }

    @Test
    public void getBestillingerOk() {
        when(mapperFacade.mapAsList(anyList(), eq(RsBestilling.class)))
                .thenReturn(singletonList(RsBestilling.builder().id(BESTILLING_ID).build()));

        RsBestillingStatus bestilling = bestillingController.getBestillinger(GRUPPE_ID).get(0);

        verify(bestillingService).fetchBestillingerByGruppeId(GRUPPE_ID);
        verify(mapperFacade).mapAsList(anyList(), eq(RsBestilling.class));

        assertThat(bestilling.getId(), is(equalTo(BESTILLING_ID)));
    }

    @Test
    public void stopBestillingProgressOk() {
        when(bestillingService.cancelBestilling(BESTILLING_ID)).thenReturn(Bestilling.builder().build());
        bestillingController.stopBestillingProgress(BESTILLING_ID);

        verify(bestillingService).cancelBestilling(BESTILLING_ID);
        verify(mapperFacade).map(any(Bestilling.class), eq(RsBestilling.class));
    }

    @Test
    public void gjenopprettBestillingOk() {

        when(bestillingService.createBestillingForGjenopprett(eq(BESTILLING_ID), anyList()))
                .thenReturn(Bestilling.builder().build());
        when(mapperFacade.map(any(Bestilling.class), eq(RsBestilling.class)))
                .thenReturn(RsBestilling.builder().id(BESTILLING_ID).build());

        RsBestilling bestilling = bestillingController.gjenopprettBestilling(BESTILLING_ID, null);

        assertThat(bestilling.getId(), is(equalTo(BESTILLING_ID)));
        verify(dollyBestillingService).gjenopprettBestillingAsync(any(Bestilling.class));
        verify(mapperFacade).map(any(Bestilling.class), eq(RsBestilling.class));
    }

    @Test
    public void malBestillingNavnOk() {
        bestillingController.getMalBestillinger();

        verify(bestillingService).fetchMalBestillinger();
        verify(mapperFacade).mapAsList(anyList(), eq(RsBestilling.class));
    }
}