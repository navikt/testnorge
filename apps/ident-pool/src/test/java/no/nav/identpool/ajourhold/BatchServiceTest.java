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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
            this.entity = (Ajourhold) invocation.getArguments()[0];
            return null;
        }).when(ajourholdRepository).update(any(Ajourhold.class));
        doNothing().when(ajourholdRepository).delete(any(Ajourhold.class));
    }

    @Test
    void batchKjorer() {
        doReturn(true).when(ajourholdService).checkCriticalAndGenerate();
        batchService.startGeneratingIdentsBatch();
        assertThat(entity.getStatus(), is(BatchStatus.COMPLETED));
    }

    @Test
    void batchFeiler() {
        String exception = "Unique exception";
        doThrow(new RuntimeException(exception)).when(ajourholdService).checkCriticalAndGenerate();
        batchService.startGeneratingIdentsBatch();
        assertThat(entity.getStatus(), is(BatchStatus.FAILED));
        assertThat(entity.getFeilmelding(), containsString(exception));
        Ajourhold prev = entity;
        batchService.startGeneratingIdentsBatch();
        assertThat(prev.getIdentity(), is(entity.getIdentity()));
        assertThat(prev.getStatus(), is(entity.getStatus()));
    }

    @Test
    void batchKjorerMenGenerererIngenIdenter() {
        doReturn(false).when(ajourholdService).checkCriticalAndGenerate();
        batchService.startGeneratingIdentsBatch();
        verify(ajourholdRepository, times(1)).delete(any(Ajourhold.class));
    }
}
