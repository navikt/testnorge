package no.nav.identpool.batch.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.batch.runtime.BatchStatus;
import javax.jms.JMSException;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import no.nav.identpool.batch.repository.BatchEntity;
import no.nav.identpool.batch.repository.BatchRepository;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final LocalDate defaultStartDate = LocalDate.of(1910, 1, 1);
    private final LocalDate defaultStopDate = LocalDate.now().plusDays(1);

    private final BatchRepository batchRepository;

    private final IdentService identService;

    public void startBatch() {
        LocalDate startDate = getStartDate();
        cleanupRepository();
        BatchEntity entity = BatchEntity.builder()
                .startDato(startDate)
                .sluttDato(startDate)
                .sistOppdatert(LocalDateTime.now())
                .status(BatchStatus.STARTED)
                .build();
        batchRepository.save(entity);
        this.execute(entity);
    }

    private LocalDate getStartDate() {
        BatchEntity entity = batchRepository.findFirstWhereStatusIsStartedOrderBySluttDatoDesc();
        return entity != null ? entity.getSluttDato().minusDays(1) : defaultStartDate;
    }

    private void cleanupRepository() {
        List<BatchEntity> stopped = batchRepository.findAllByWhereStatusIsStarted();
        for (BatchEntity entity : stopped) {
            entity.setFeilmelding("Batch did not finish");
            entity.setStatus(BatchStatus.FAILED);
            batchRepository.save(entity);
        }
    }

    private void execute(BatchEntity batchEntity) {
        try {
            while (batchEntity.getSluttDato().isBefore(defaultStopDate)) {
                identService.findAndSave(batchEntity.getSluttDato());
                batchEntity.setSluttDato(batchEntity.getSluttDato().plusDays(1));
                batchEntity.setSistOppdatert(LocalDateTime.now());
                batchEntity = batchRepository.save(batchEntity);
            }
        } catch (JMSException e) {
            batchEntity.setFeilmelding(e.getMessage());
            batchEntity.setStatus(BatchStatus.FAILED);
            batchRepository.save(batchEntity);
        }
    }
}
