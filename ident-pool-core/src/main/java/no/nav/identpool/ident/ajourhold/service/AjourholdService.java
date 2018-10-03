package no.nav.identpool.ident.ajourhold.service;

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

    public void startBatch() {
        AjourholdEntity entity = AjourholdEntity.builder()
                .sistOppdatert(LocalDateTime.now())
                .status(BatchStatus.STARTED)
                .build();
        ajourholdRepository.update(entity);
        this.execute(entity);
    }

    private void execute(AjourholdEntity ajourholdEntity) {
        try {
            identService.checkCritcalAndGenerate();
            ajourholdEntity.setStatus(BatchStatus.COMPLETED);
            ajourholdRepository.update(ajourholdEntity);
        } catch (Exception e) {
            ajourholdEntity.setFeilmelding(e.getMessage());
            ajourholdEntity.setStatus(BatchStatus.FAILED);
            ajourholdRepository.update(ajourholdEntity);
            throw e;
        }
    }
}
