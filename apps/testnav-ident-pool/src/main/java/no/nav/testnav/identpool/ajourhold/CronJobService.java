package no.nav.testnav.identpool.ajourhold;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "generer.identer.enable", matchIfMissing = true)
public class CronJobService {

    private final BatchService batchService;

    @Timed(value = "ident-pool.ajourhold", longTask = true)
    @Scheduled(cron = "0 0 01 * * *")
    public void execute() {

        log.info("Starter data mining ...");
        batchService.startGeneratingIdents(null)
                        .subscribe(status -> log.info("Data mining avsluttet {}", status.getStatus()));
    }

    @Scheduled(cron = "0 0 04 * * *")
    public void checkProdStatus() {

        log.info("Starter vasking av identer mot prod");
        batchService.updateDatabaseWithProdStatus(null)
                        .subscribe(status -> log.info("Vasking av proddata avsluttet: {}", status.getStatus()));
    }
}