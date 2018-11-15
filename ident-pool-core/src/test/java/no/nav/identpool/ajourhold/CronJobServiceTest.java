package no.nav.identpool.ajourhold;

import no.nav.identpool.ajourhold.service.AjourholdService;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class CronJobServiceTest {

    //TODO Test bedre
    @Test
    void startJob() {
        AjourholdService ajourholdService = mock(AjourholdService.class);
        doNothing().when(ajourholdService).startBatch();
        new CronJobService(ajourholdService).execute();
        verify(ajourholdService, times(1)).startBatch();
    }
}
