package no.nav.identpool.ajourhold;

import no.nav.identpool.domain.Ajourhold;
import no.nav.identpool.repository.AjourholdRepository;
import no.nav.identpool.test.mockito.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;

import javax.batch.runtime.BatchStatus;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

    @Spy
    private AjourholdRepository ajourholdRepository;

    @Mock
    private AjourholdService ajourholdService;

    @InjectMocks
    private BatchService batchService;

    private Ajourhold entity;

    @BeforeEach
    void init() {
        doAnswer((Answer<Void>) invocation -> {
            Ajourhold ajourhold = (Ajourhold) invocation.getArguments()[0];
            this.entity = ajourhold;
            return null;
        }).when(ajourholdRepository).update(any(Ajourhold.class));
        doNothing().when(ajourholdRepository).delete(any(Ajourhold.class));

//        batchService = new BatchService(ajourholdRepository, ajourholdService);
    }

    @Test
    void batchKjorer() {
        when(ajourholdService.checkCriticalAndGenerate()).thenReturn(true);
        batchService.startBatch();
        assertThat(entity.getStatus(), is(BatchStatus.COMPLETED));
    }

    @Test
    void batchFeiler() {
        String exception = "Unique exception";
        when(ajourholdService.checkCriticalAndGenerate()).thenThrow(new RuntimeException(exception));
        batchService.startBatch();
        assertThat(entity.getStatus(), is(BatchStatus.FAILED));
        assertThat(entity.getFeilmelding(), containsString(exception));
        Ajourhold prev = entity;
        batchService.startBatch();
        assertThat(prev, is(entity));
    }

    @Test
    void batchKjorerMenGenerererIngenIdenter() {
        when(ajourholdService.checkCriticalAndGenerate()).thenReturn(false);
        batchService.startBatch();
        verify(ajourholdRepository, times(1)).delete(any(Ajourhold.class));
    }
}
