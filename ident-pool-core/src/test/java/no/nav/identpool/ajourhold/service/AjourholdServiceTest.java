package no.nav.identpool.ajourhold.service;

import no.nav.identpool.ajourhold.repository.AjourholdEntity;
import no.nav.identpool.ajourhold.repository.AjourholdRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.batch.runtime.BatchStatus;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AjourholdServiceTest {

    @Spy
    private AjourholdRepository ajourholdRepository;

    private IdentDBService identDBService;

    private AjourholdService ajourholdService;

    private AjourholdEntity entity;

    @Before
    public void init() {
        identDBService = mock(IdentDBService.class);

        doAnswer((Answer<Void>) invocation -> {
            AjourholdEntity entity = (AjourholdEntity) invocation.getArguments()[0];
            copy(entity);
            return null;
        }).when(ajourholdRepository).update(any(AjourholdEntity.class));
        doNothing().when(ajourholdRepository).delete(any(AjourholdEntity.class));

        ajourholdService = new AjourholdService(ajourholdRepository, identDBService);
    }

    public void copy(AjourholdEntity ajourholdEntity) {
        this.entity = ajourholdEntity;
    }

    @Test
    public void batchKjorer() {
        when(identDBService.checkCriticalAndGenerate()).thenReturn(1);
        ajourholdService.startBatch();
        assertThat(entity.getStatus(), is(BatchStatus.COMPLETED));
    }

    @Test
    public void batchFeiler() {
        String exception = "Unique exception";
        when(identDBService.checkCriticalAndGenerate()).thenThrow(new RuntimeException(exception));
        ajourholdService.startBatch();
        assertThat(entity.getStatus(), is(BatchStatus.FAILED));
        assertThat(entity.getFeilmelding(), containsString(exception));
        AjourholdEntity prev = entity;
        ajourholdService.startBatch();
        assertThat(prev, is(entity));
    }

    @Test
    public void batchKjorerMenGenerererIngenIdenter() {
        when(identDBService.checkCriticalAndGenerate()).thenReturn(0);
        ajourholdService.startBatch();
        verify(ajourholdRepository, times(1)).delete(any(AjourholdEntity.class));
    }
}
