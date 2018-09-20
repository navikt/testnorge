package no.nav.identpool.ident.ajourhold.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.batch.runtime.BatchStatus;
import javax.jms.JMSException;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import no.nav.identpool.ident.ajourhold.repository.AjourholdEntity;
import no.nav.identpool.ident.ajourhold.repository.AjourholdRepository;

@Service
@RequiredArgsConstructor
public class AjourholdService {

    private final LocalDate defaultStartDate = LocalDate.of(1910, 1, 1);

    private final AjourholdRepository batchRepository;
    private final IdentService identService;

    public void startBatch() {
        LocalDate startDate = getStartDate();
        cleanupRepository();
        AjourholdEntity entity = AjourholdEntity.builder()
                .startDato(startDate)
                .sluttDato(startDate)
                .sistOppdatert(LocalDateTime.now())
                .status(BatchStatus.STARTED)
                .build();
        batchRepository.save(entity);
        this.execute(entity);
    }

    private LocalDate getStartDate() {
        AjourholdEntity entity = batchRepository.findFirstWhereStatusIsStartedOrderBySluttDatoDesc();
        return entity != null && entity.getSluttDato() != null ? entity.getSluttDato().minusDays(1) : defaultStartDate;
    }

    private void cleanupRepository() {
        List<AjourholdEntity> stopped = batchRepository.findAllByWhereStatusIsStarted();
        for (AjourholdEntity entity : stopped) {
            entity.setFeilmelding("Batch did not finish");
            entity.setStatus(BatchStatus.FAILED);
            batchRepository.save(entity);
        }
    }

    private void execute(AjourholdEntity batchEntity) {
        int numberOfDates = 3;
        LocalDate defaultStopDate = LocalDate.now().plusDays(numberOfDates);
        try {
            while (batchEntity.getSluttDato().isBefore(defaultStopDate)) {
                identService.generateForDates(batchEntity.getSluttDato(), numberOfDates);
                batchEntity.setSluttDato(batchEntity.getSluttDato().plusDays(numberOfDates));
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
