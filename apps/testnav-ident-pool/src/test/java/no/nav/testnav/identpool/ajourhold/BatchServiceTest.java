package no.nav.testnav.identpool.ajourhold;

import no.nav.testnav.identpool.domain.Ajourhold;
import no.nav.testnav.identpool.domain.BatchStatus;
import no.nav.testnav.identpool.repository.AjourholdRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void batchMining_KjorerOK() {

        when(ajourholdRepository.save(any(Ajourhold.class)))
                .thenReturn(Mono.just(prepareAjourhold(BatchStatus.MINING_STARTED)))
                .thenReturn(Mono.just(prepareAjourhold(BatchStatus.MINING_COMPLETED)));
        when(ajourholdService.checkCriticalAndGenerate(null))
                .thenReturn(Mono.just("Identer opprettet"));

        batchService.startGeneratingIdents(null)
                .as(StepVerifier::create)
                .assertNext(ajourhold -> assertThat(ajourhold.getStatus(), Matchers.is(BatchStatus.MINING_COMPLETED)))
                .verifyComplete();

        verify(ajourholdRepository, times(2)).save(ajourholdArgumentCaptor.capture());
        assertThat(ajourholdArgumentCaptor.getAllValues().get(0).getStatus(), is(BatchStatus.MINING_STARTED));
        assertThat(ajourholdArgumentCaptor.getAllValues().get(1).getStatus(), is(BatchStatus.MINING_COMPLETED));
    }

    @Test
    void batchMining_Feiler() {

        when(ajourholdRepository.save(any(Ajourhold.class)))
                .thenReturn(Mono.just(prepareAjourhold(BatchStatus.MINING_STARTED)))
                .thenReturn(Mono.just(prepareAjourhold(BatchStatus.MINING_FAILED)));
        when(ajourholdService.checkCriticalAndGenerate(null))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Feil under generering av identer"
                ));

        batchService.startGeneratingIdents(null)
                .as(StepVerifier::create)
                .assertNext(ajourhold -> assertThat(ajourhold.getStatus(), Matchers.is(BatchStatus.MINING_FAILED)))
                .verifyComplete();

        verify(ajourholdRepository, times(2)).save(ajourholdArgumentCaptor.capture());
        assertThat(ajourholdArgumentCaptor.getAllValues().get(0).getStatus(), is(BatchStatus.MINING_STARTED));
        assertThat(ajourholdArgumentCaptor.getAllValues().get(1).getStatus(), is(BatchStatus.MINING_FAILED));
    }

    @Test
    void batchProdClean_KjorerOK() {

        when(ajourholdRepository.save(any(Ajourhold.class)))
                .thenReturn(Mono.just(prepareAjourhold(BatchStatus.CLEAN_STARTED)))
                .thenReturn(Mono.just(prepareAjourhold(BatchStatus.CLEAN_COMPLETED)));
        when(ajourholdService.getIdentsAndCheckProd(any()))
                .thenReturn(Mono.just("Ryddet 1 ident i produksjon"));

        batchService.updateDatabaseWithProdStatus(any())
                .as(StepVerifier::create)
                .assertNext(ajourhold -> assertThat(ajourhold.getStatus(), Matchers.is(BatchStatus.CLEAN_COMPLETED)))
                .verifyComplete();

        verify(ajourholdRepository, times(2)).save(ajourholdArgumentCaptor.capture());
        assertThat(ajourholdArgumentCaptor.getAllValues().get(0).getStatus(), is(BatchStatus.CLEAN_STARTED));
        assertThat(ajourholdArgumentCaptor.getAllValues().get(1).getStatus(), is(BatchStatus.CLEAN_COMPLETED));
    }

    @Test
    void batchProdClean_Feiler() {

        when(ajourholdRepository.save(any(Ajourhold.class)))
                .thenReturn(Mono.just(prepareAjourhold(BatchStatus.CLEAN_STARTED)))
                .thenReturn(Mono.just(prepareAjourhold(BatchStatus.CLEAN_FAILED)));
        when(ajourholdService.getIdentsAndCheckProd(null))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Feil under generering av identer"
                ));

        batchService.updateDatabaseWithProdStatus(null)
                .as(StepVerifier::create)
                .assertNext(ajourhold -> assertThat(ajourhold.getStatus(), Matchers.is(BatchStatus.CLEAN_FAILED)))
                .verifyComplete();

        verify(ajourholdRepository, times(2)).save(ajourholdArgumentCaptor.capture());
        assertThat(ajourholdArgumentCaptor.getAllValues().get(0).getStatus(), is(BatchStatus.CLEAN_STARTED));
        assertThat(ajourholdArgumentCaptor.getAllValues().get(1).getStatus(), is(BatchStatus.CLEAN_FAILED));
    }

    private static Ajourhold prepareAjourhold(BatchStatus status) {

        return Ajourhold.builder()
                .sistOppdatert(LocalDateTime.now())
                .status(status)
                .build();
    }
}
