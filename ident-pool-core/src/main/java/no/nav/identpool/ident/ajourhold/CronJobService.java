package no.nav.identpool.ident.ajourhold;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.ident.ajourhold.service.AjourholdService;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "generer.identer.enable", matchIfMissing = true)
public class CronJobService {

    private final AjourholdService ajourholdService;

    @Scheduled(fixedDelay = 60000)
    public void execute() {
        ajourholdService.startBatch();
    }
}