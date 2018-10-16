package no.nav.identpool.ident.ajourhold;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import no.nav.identpool.ident.ajourhold.service.AjourholdService;

@Component
@RequiredArgsConstructor
public class CronJobService {

    private final AjourholdService ajourholdService;

    // kommenter vekk scheduled for lokalkjøring så man ikke ddos-er tps
    // @Scheduled(fixedDelay = 60000)
    public void execute() {
        ajourholdService.startBatch();
    }
}