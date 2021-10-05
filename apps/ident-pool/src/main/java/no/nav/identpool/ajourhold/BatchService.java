package no.nav.identpool.ajourhold;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.domain.Ajourhold;
import no.nav.identpool.repository.AjourholdRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.batch.runtime.BatchStatus;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

    private final AjourholdRepository ajourholdRepository;
    private final AjourholdService ajourholdService;

    public void startGeneratingIdentsBatch() {
        Ajourhold entity = Ajourhold.builder()
                .sistOppdatert(LocalDateTime.now())
                .status(BatchStatus.STARTED)
                .build();
        ajourholdRepository.update(entity);
        this.runNewAjourhold(entity);
    }

    public void updateDatabaseWithProdStatus() {
        ajourholdService.getIdentsAndCheckProd();
    }

    private void runNewAjourhold(Ajourhold ajourhold) {
        try {
            boolean newIdentsAdded = ajourholdService.checkCriticalAndGenerate();
            if (newIdentsAdded) {
                ajourhold.setStatus(BatchStatus.COMPLETED);
                ajourholdRepository.update(ajourhold);
            } else {
                ajourholdRepository.delete(ajourhold);
            }
        } catch (Exception e) {
            String exceptionString = ExceptionUtils.getStackTrace(e);
            if (exceptionString.length() > 1023) {
                exceptionString = exceptionString.substring(0, 1023);
            }
            ajourhold.setFeilmelding(exceptionString);
            ajourhold.setStatus(BatchStatus.FAILED);
            ajourholdRepository.update(ajourhold);
            log.warn(e.getMessage());
        }
    }
}
