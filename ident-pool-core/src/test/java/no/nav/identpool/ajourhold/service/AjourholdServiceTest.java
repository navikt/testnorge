package no.nav.identpool.ajourhold.service;

import no.nav.identpool.domain.AjourholdEntity;
import no.nav.identpool.repository.AjourholdRepository;
import no.nav.identpool.test.mockito.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;

import javax.batch.runtime.BatchStatus;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AjourholdServiceTest {

    @Spy
    private AjourholdRepository ajourholdRepository;

    private IdentDBService identDBService;

    private AjourholdService ajourholdService;

    private AjourholdEntity entity;

    @BeforeEach
    void init() {
        identDBService = mock(IdentDBService.class);

        doAnswer((Answer<Void>) invocation -> {
            AjourholdEntity entity = (AjourholdEntity) invocation.getArguments()[0];
            copy(entity);
            return null;
        }).when(ajourholdRepository).update(any(AjourholdEntity.class));
        doNothing().when(ajourholdRepository).delete(any(AjourholdEntity.class));

        ajourholdService = new AjourholdService(ajourholdRepository, identDBService);
    }

    void copy(AjourholdEntity ajourholdEntity) {
        this.entity = ajourholdEntity;
    }

    @Test
    void batchKjorer() {
        when(identDBService.checkCriticalAndGenerate()).thenReturn(true);
        ajourholdService.startBatch();
        assertThat(entity.getStatus(), is(BatchStatus.COMPLETED));
    }

    @Test
    void batchFeiler() {
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
    void batchKjorerMenGenerererIngenIdenter() {
        when(identDBService.checkCriticalAndGenerate()).thenReturn(false);
        ajourholdService.startBatch();
        verify(ajourholdRepository, times(1)).delete(any(AjourholdEntity.class));
    }
}
