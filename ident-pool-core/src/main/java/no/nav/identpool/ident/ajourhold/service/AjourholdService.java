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

    private final AjourholdRepository batchRepository;
    private final IdentDbService identService;

    public void startBatch() {
        AjourholdEntity entity = AjourholdEntity.builder()
                .sistOppdatert(LocalDateTime.now())
                .status(BatchStatus.STARTED)
                .build();
        batchRepository.save(entity);
        this.execute(entity);
    }

    private void execute(AjourholdEntity ajourholdEntity) {
        long time = System.nanoTime();
        identService.checkCritcalAndGenerate();
    }
}
