package no.nav.dolly.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultSet.RsBestilling;
import no.nav.dolly.domain.resultSet.RsBestillingProgress;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BestillingControllerTest {

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private BestillingProgressService progressService;

    @Mock
    private BestillingService bestillingService;

    @InjectMocks
    private BestillingController controller;

    @Test
    public void checkBestillingsstatus_oppdatererMedPersonstatusOrReturnererBestilling() {
        RsBestilling rsBestilling = new RsBestilling();
        BestillingProgress prog = new BestillingProgress();
        RsBestillingProgress rsProg = new RsBestillingProgress();
        List<RsBestillingProgress> rsProgList = Arrays.asList(rsProg);
        List<BestillingProgress> progList = Arrays.asList(prog);

        when(progressService.fetchProgressButReturnEmptyListIfBestillingsIdIsNotFound(any())).thenReturn(progList);
        when(mapperFacade.mapAsList(progList, RsBestillingProgress.class)).thenReturn(rsProgList);
        when(bestillingService.fetchBestillingById(any())).thenReturn(new Bestilling());
        when(mapperFacade.map(any(), any())).thenReturn(rsBestilling);

        RsBestilling res = controller.checkBestillingsstatus(1l);

        assertThat(res, is(rsBestilling));
        assertThat(res.getPersonStatus().get(0), is(rsProg));
    }
}