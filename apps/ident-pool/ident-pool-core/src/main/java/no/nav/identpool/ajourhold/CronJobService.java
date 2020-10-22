package no.nav.identpool.ajourhold;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "generer.identer.enable", matchIfMissing = true)
public class CronJobService {

    private final BatchService batchService;

    @Timed(value = "ident-pool.ajourhold", longTask = true)
    @Scheduled(cron = "0 0 20 * * *")
    public void execute() {
        batchService.startGeneratingIdentsBatch();
    }

    @Scheduled(cron = "0 0 22 * * *")
    public void checkProdStatus() {
        batchService.updateDatabaseWithProdStatus();
    }
}