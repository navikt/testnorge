package no.nav.registre.orkestratoren.batch.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import no.nav.registre.orkestratoren.service.OrkestreingsService;

@Component
@EnableScheduling
@Slf4j
@Profile("prod")
@RequiredArgsConstructor
public class OrkestreingJob {

    private final OrkestreingsService orkestreingsService;

    @Scheduled(cron = "0 0 7 * * *")
    public void sykemeldingOrkestreringsBatch() {
        orkestreingsService.orkisterSykemeldinger();
    }
}
