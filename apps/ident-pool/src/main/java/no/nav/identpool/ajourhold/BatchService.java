package no.nav.identpool.ajourhold;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.domain.Ajourhold;
import no.nav.identpool.domain.BatchStatus;
import no.nav.identpool.repository.AjourholdRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

    private final AjourholdRepository ajourholdRepository;
    private final AjourholdService ajourholdService;

    public void startGeneratingIdentsBatch() {

        var ajourhold = new Ajourhold();

        try {
            updateStatus(ajourhold, BatchStatus.MINING_STARTED);
            ajourholdService.checkCriticalAndGenerate();
            updateStatus(ajourhold, BatchStatus.MINING_COMPLETE);

        } catch (Exception e) {
            ajourhold.setFeilmelding(ExceptionUtils.getStackTrace(e).substring(0, 1023));
            updateStatus(ajourhold, BatchStatus.MINING_FAILED);
            log.error(e.getMessage(), e);
        }
    }

    public void updateDatabaseWithProdStatus() {

        var ajourhold = new Ajourhold();

        try {
            updateStatus(ajourhold, BatchStatus.CLEAN_STARTED);
            ajourholdService.getIdentsAndCheckProd();
            updateStatus(ajourhold, BatchStatus.CLEAN_COMPLETED);

        } catch (Exception e) {
            ajourhold.setFeilmelding(ExceptionUtils.getStackTrace(e).substring(0, 1023));
            updateStatus(ajourhold, BatchStatus.CLEAN_FAILED);
            log.error(e.getMessage(), e);
        }
    }

    private void updateStatus(Ajourhold ajourhold, BatchStatus status) {

        ajourhold.setSistOppdatert(LocalDateTime.now());
        ajourhold.setStatus(status);
        ajourholdRepository.save(ajourhold);
    }
}