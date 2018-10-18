package no.nav.identpool.ident.ajourhold.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import javax.batch.runtime.BatchStatus;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import no.nav.identpool.ident.ajourhold.repository.AjourholdEntity;
import no.nav.identpool.ident.ajourhold.repository.AjourholdRepository;

@Service
@RequiredArgsConstructor
public class AjourholdService {

    private final AjourholdRepository ajourholdRepository;
    private final IdentDBService identService;

    private StringWriter writer = new StringWriter();
    private PrintWriter printWriter= new PrintWriter(writer);


    public void startBatch() {
        AjourholdEntity entity = AjourholdEntity.builder()
                .sistOppdatert(LocalDateTime.now())
                .status(BatchStatus.STARTED)
                .build();
        ajourholdRepository.update(entity);
        this.run(entity);
    }

    private void run(AjourholdEntity ajourholdEntity) {
        try {
            identService.checkCriticalAndGenerate();
            ajourholdEntity.setStatus(BatchStatus.COMPLETED);
            ajourholdRepository.update(ajourholdEntity);
        } catch (Exception e) {
            e.printStackTrace(printWriter);
            ajourholdEntity.setFeilmelding(printWriter.toString());
            ajourholdEntity.setStatus(BatchStatus.FAILED);
            ajourholdRepository.update(ajourholdEntity);
            throw e;
        }
    }
}
