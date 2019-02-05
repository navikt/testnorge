package no.nav.identpool.ajourhold;

import io.micrometer.core.annotation.Timed;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "generer.identer.enable", matchIfMissing = true)
public class CronJobService {
    private final BatchService batchService;

    @Timed(value = "ident-pool.ajourhold", longTask = true)
    @Scheduled(fixedDelay = 60000)
    public void execute() {
        batchService.startBatch();
    }
}