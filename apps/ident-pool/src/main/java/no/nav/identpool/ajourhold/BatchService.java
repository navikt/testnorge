package no.nav.identpool.ajourhold;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.domain.Ajourhold;
import no.nav.identpool.repository.AjourholdRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.batch.runtime.BatchStatus;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

    private final AjourholdRepository ajourholdRepository;
    private final AjourholdService ajourholdService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void startGeneratingIdentsBatch() {
        Ajourhold entity = Ajourhold.builder()
                .sistOppdatert(LocalDateTime.now())
                .status(BatchStatus.STARTED)
                .build();
        ajourholdRepository.save(entity);
        this.runNewAjourhold(entity);
    }

    public void updateDatabaseWithProdStatus() {
        ajourholdService.getIdentsAndCheckProd();
    }

    private void runNewAjourhold(Ajourhold ajourhold) {

        try {
            ajourholdService.checkCriticalAndGenerate();
            ajourhold.setStatus(BatchStatus.COMPLETED);
            ajourholdRepository.save(ajourhold);

        } catch (Exception e) {

            ajourhold.setFeilmelding(ExceptionUtils.getStackTrace(e).substring(0, 1023));
            ajourhold.setStatus(BatchStatus.FAILED);
            ajourholdRepository.save(ajourhold);
            log.error(e.getMessage(), e);
        }
    }
}