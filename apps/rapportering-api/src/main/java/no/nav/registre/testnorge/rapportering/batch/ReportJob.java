package no.nav.registre.testnorge.rapportering.batch;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.rapportering.service.ReportService;

@Component
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class ReportJob {

    private final ReportService service;

    @Scheduled(cron = "0 0 8 * * *")
    public void publishReportes() {
        service.publishAll();
    }
}
