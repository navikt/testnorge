package no.nav.identpool.ident.batch.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.batch.runtime.BatchStatus;
import javax.jms.JMSException;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import no.nav.identpool.ident.batch.repository.BatchEntity;
import no.nav.identpool.ident.batch.repository.BatchRepository;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final LocalDate defaultStartDate = LocalDate.of(1910, 1, 1);

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
        return entity != null && entity.getSluttDato() != null ? entity.getSluttDato().minusDays(1) : defaultStartDate;
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
        LocalDate defaultStopDate = LocalDate.now().plusDays(1);
        try {
            while (batchEntity.getSluttDato().isBefore(defaultStopDate)) {
                identService.generateForDates(batchEntity.getSluttDato(), batchEntity.getSluttDato().plusDays(1),  batchEntity.getSluttDato().plusDays(2));
                System.exit(0);
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
