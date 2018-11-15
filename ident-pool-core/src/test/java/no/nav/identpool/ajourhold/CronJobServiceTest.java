package no.nav.identpool.ajourhold;

import no.nav.identpool.ajourhold.service.AjourholdService;

import org.junit.Test;

import static org.mockito.Mockito.*;

public class CronJobServiceTest {

    //TODO Test
    @Test
    public void startJob() {
        AjourholdService ajourholdService = mock(AjourholdService.class);
        doNothing().when(ajourholdService).startBatch();
        new CronJobService(ajourholdService).execute();
        verify(ajourholdService, times(1)).startBatch();
    }
}
