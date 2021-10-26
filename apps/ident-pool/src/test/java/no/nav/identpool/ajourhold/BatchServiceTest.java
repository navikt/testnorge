package no.nav.identpool.ajourhold;

import no.nav.identpool.domain.Ajourhold;
import no.nav.identpool.domain.BatchStatus;
import no.nav.identpool.repository.AjourholdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

    @Captor
    private ArgumentCaptor<Ajourhold> ajourholdArgumentCaptor;

    @Mock
    private AjourholdRepository ajourholdRepository;

    @Mock
    private AjourholdService ajourholdService;

    @InjectMocks
    private BatchService batchService;

    @BeforeEach
    void init() {
        ajourholdArgumentCaptor = ArgumentCaptor.forClass(Ajourhold.class);
    }

    @Test
    void batchKjorer() {

        batchService.startGeneratingIdentsBatch();

        verify(ajourholdRepository, times(2)).save(ajourholdArgumentCaptor.capture());

        assertThat(ajourholdArgumentCaptor.getAllValues().stream().reduce((first, second) -> second).get().getStatus(), is(BatchStatus.MINING_COMPLETE));
    }

    @Test
    void batchFeiler() {

        String exception = "Unique exception";
        doThrow(new RuntimeException(exception)).when(ajourholdService).checkCriticalAndGenerate();

        batchService.startGeneratingIdentsBatch();

        verify(ajourholdRepository, times(2)).save(ajourholdArgumentCaptor.capture());

        assertThat(ajourholdArgumentCaptor.getAllValues().stream().reduce((first, second) -> second).get().getStatus(), is(BatchStatus.MINING_FAILED));
        assertThat(ajourholdArgumentCaptor.getAllValues().stream().reduce((first, second) -> second).get().getFeilmelding(), containsString(exception));
    }
}
