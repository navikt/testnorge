package no.nav.identpool.ajourhold;

import java.time.LocalDateTime;
import javax.batch.runtime.BatchStatus;

import no.nav.identpool.domain.Ajourhold;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.exception.ExceptionUtils;
import no.nav.identpool.repository.AjourholdRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

    private final AjourholdRepository ajourholdRepository;
    private final AjourholdService ajourholdService;

    private int waitCounter = 0;

    public void startBatch() {
        if (waitCounter > 0) {
            waitCounter--;
            return;
        }
        Ajourhold entity = Ajourhold.builder()
                .sistOppdatert(LocalDateTime.now())
                .status(BatchStatus.STARTED)
                .build();
        ajourholdRepository.update(entity);
        this.run(entity);
    }

    private void run(Ajourhold ajourhold) {
        try {
            boolean newIdentsAdded = ajourholdService.checkCriticalAndGenerate();
            if (newIdentsAdded) {
                ajourhold.setStatus(BatchStatus.COMPLETED);
                ajourholdRepository.update(ajourhold);
            } else {
                ajourholdRepository.delete(ajourhold);
            }
        } catch (Exception e) {
            waitCounter = 10;
            String exceptionString = ExceptionUtils.getFullStackTrace(e);
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
