package no.nav.identpool.ident.batch;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.ident.batch.service.BatchService;

@Component
@RequiredArgsConstructor
public class CronJobService {

    private final BatchService batchService;

    @Scheduled(fixedDelay = 1000)
    public void execute() {
        batchService.startBatch();
    }
}