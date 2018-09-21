package no.nav.identpool.ident.ajourhold;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import no.nav.identpool.ident.ajourhold.service.AjourholdService;

@Component
@RequiredArgsConstructor
public class CronJobService {

    private final AjourholdService ajourholdService;

    public void execute() {
        ajourholdService.startBatch();
    }
}