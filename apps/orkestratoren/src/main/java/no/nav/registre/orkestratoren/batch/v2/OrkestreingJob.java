package no.nav.registre.orkestratoren.batch.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserFrikortRequest;
import no.nav.registre.orkestratoren.service.SykemeldingOrkestreringsService;

@Component
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class OrkestreingJob {

    private final SykemeldingOrkestreringsService sykemeldingOrkestreringsService;

    @Scheduled(cron = "0 0 7 * * *")
    public void sykemeldingOrkestreringsBatch() {
        sykemeldingOrkestreringsService.orkistrer();
    }
}
