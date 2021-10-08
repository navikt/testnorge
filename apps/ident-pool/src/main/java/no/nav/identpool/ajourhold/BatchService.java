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
            ajourhold.setSistOppdatert(LocalDateTime.now());
            ajourhold.setStatus(BatchStatus.MINING_STARTED);
            ajourholdRepository.save(ajourhold);

            ajourholdService.checkCriticalAndGenerate();

            ajourhold.setSistOppdatert(LocalDateTime.now());
            ajourhold.setStatus(BatchStatus.MINING_COMPLETE);
            ajourholdRepository.save(ajourhold);

        } catch (Exception e) {

            ajourhold.setFeilmelding(ExceptionUtils.getStackTrace(e).substring(0, 1023));

            ajourhold.setSistOppdatert(LocalDateTime.now());
            ajourhold.setStatus(BatchStatus.MINING_FAILED);
            ajourholdRepository.save(ajourhold);

            log.error(e.getMessage(), e);
        }
    }

    public void updateDatabaseWithProdStatus() {

        var ajourhold = new Ajourhold();

        try {
            ajourhold.setSistOppdatert(LocalDateTime.now());
            ajourhold.setStatus(BatchStatus.CLEAN_STARTED);
            ajourholdRepository.save(ajourhold);

            ajourholdService.getIdentsAndCheckProd();

            ajourhold.setSistOppdatert(LocalDateTime.now());
            ajourhold.setStatus(BatchStatus.CLEAN_COMPLETED);
            ajourholdRepository.save(ajourhold);

        } catch (Exception e) {

            ajourhold.setFeilmelding(ExceptionUtils.getStackTrace(e).substring(0, 1023));

            ajourhold.setSistOppdatert(LocalDateTime.now());
            ajourhold.setStatus(BatchStatus.CLEAN_FAILED);
            ajourholdRepository.save(ajourhold);

            log.error(e.getMessage(), e);
        }
    }
}