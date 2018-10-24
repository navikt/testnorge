package no.nav.identpool.ajourhold.service;

import java.time.LocalDateTime;
import javax.batch.runtime.BatchStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.exception.ExceptionUtils;
import no.nav.identpool.ajourhold.repository.AjourholdEntity;
import no.nav.identpool.ajourhold.repository.AjourholdRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AjourholdService {

    private final AjourholdRepository ajourholdRepository;
    private final IdentDBService identService;

    private int waitCounter = 0;

    public void startBatch() {
        if (waitCounter > 0) {
            waitCounter--;
            return;
        }
        AjourholdEntity entity = AjourholdEntity.builder()
                .sistOppdatert(LocalDateTime.now())
                .status(BatchStatus.STARTED)
                .build();
        ajourholdRepository.update(entity);
        this.run(entity);
    }

    private void run(AjourholdEntity ajourholdEntity) {
        try {
            int sjekketITps = identService.checkCriticalAndGenerate();
            if (sjekketITps == 0) {
                ajourholdRepository.delete(ajourholdEntity);
            } else {
                ajourholdEntity.setStatus(BatchStatus.COMPLETED);
                ajourholdRepository.update(ajourholdEntity);
            }
        } catch (Exception e) {
            waitCounter = 10;
            String exceptionString = ExceptionUtils.getFullStackTrace(e);
            if (exceptionString.length() > 1023) {
                exceptionString = exceptionString.substring(0, 1023);
            }
            ajourholdEntity.setFeilmelding(exceptionString);
            ajourholdEntity.setStatus(BatchStatus.FAILED);
            ajourholdRepository.update(ajourholdEntity);
            log.warn(e.getMessage());
        }
    }
}
