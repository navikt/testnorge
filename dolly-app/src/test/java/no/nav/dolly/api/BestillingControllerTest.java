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

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.domain.resultset.RsBestillingProgress;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;

@RunWith(MockitoJUnitRunner.class)
public class BestillingControllerTest {

    private static final Long BESTILLING_ID = 1L;
    private static final Long BESTILLING_PROGRESS_ID = 11L;
    private static final Long GRUPPE_ID = 111L;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private BestillingProgressService progressService;

    @Mock
    private BestillingService bestillingService;

    @InjectMocks
    private BestillingController bestillingController;

    @Test
    public void checkBestillingsstatus_oppdatererMedPersonstatusOrReturnererBestilling() {
        RsBestilling rsBestilling = new RsBestilling();
        BestillingProgress prog = new BestillingProgress();
        RsBestillingProgress rsProg = new RsBestillingProgress();
        List<RsBestillingProgress> rsProgList = singletonList(rsProg);
        List<BestillingProgress> progList = singletonList(prog);

        when(progressService.fetchBestillingProgressByBestillingId(any())).thenReturn(progList);
        when(mapperFacade.mapAsList(progList, RsBestillingProgress.class)).thenReturn(rsProgList);
        when(bestillingService.fetchBestillingById(any())).thenReturn(new Bestilling());
        when(mapperFacade.map(any(), any())).thenReturn(rsBestilling);

        RsBestilling res = bestillingController.checkBestillingsstatus(BESTILLING_ID);

        assertThat(res, is(rsBestilling));
        assertThat(res.getBestillingProgress().get(0), is(rsProg));
    }

    @Test
    public void getBestillingerOk() {
        when(mapperFacade.mapAsList(anyList(), eq(RsBestilling.class)))
                .thenReturn(singletonList(RsBestilling.builder().id(BESTILLING_ID).build()));
        when(mapperFacade.mapAsList(anyList(), eq(RsBestillingProgress.class)))
                .thenReturn(singletonList(RsBestillingProgress.builder().id(BESTILLING_PROGRESS_ID).build()));

        RsBestilling bestilling = bestillingController.getBestillinger(GRUPPE_ID).get(0);

        verify(bestillingService).fetchBestillingerByGruppeId(GRUPPE_ID);
        verify(progressService).fetchBestillingProgressByBestillingId(BESTILLING_ID);
        verify(mapperFacade).mapAsList(anyList(), eq(RsBestillingProgress.class));
        verify(mapperFacade).mapAsList(anyList(), eq(RsBestilling.class));

        assertThat(bestilling.getId(), is(equalTo(BESTILLING_ID)));
        assertThat(bestilling.getBestillingProgress().get(0).getId(), is(equalTo(BESTILLING_PROGRESS_ID)));
    }

    @Test
    public void stopBestillingProgressOk() {
        when(bestillingService.cancelBestilling(BESTILLING_ID)).thenReturn(Bestilling.builder().build());
        bestillingController.stopBestillingProgress(BESTILLING_ID);

        verify(bestillingService).cancelBestilling(BESTILLING_ID);
        verify(mapperFacade).map(any(Bestilling.class), eq(RsBestilling.class));
    }
}