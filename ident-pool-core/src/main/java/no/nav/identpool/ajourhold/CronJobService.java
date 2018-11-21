package no.nav.identpool.ajourhold;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.ajourhold.service.AjourholdService;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "generer.identer.enable", matchIfMissing = true)
public class CronJobService {
    //TODO Vurder å splitte ajourhold ut i egen modul for tydeligere skille
    private final AjourholdService ajourholdService;

    @Scheduled(fixedDelay = 60000)
    public void execute() {
        ajourholdService.startBatch();
    }
}