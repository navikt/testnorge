package no.nav.registre.orgnrservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import no.nav.registre.orgnrservice.adapter.OrganisasjonAdapter;

@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class CronJobService {

    private static final int OENSKET_ANTALL = 100;
    private final OrgnummerService orgnummerService;
    private final OrganisasjonAdapter organisasjonAdapter;

    @Scheduled(cron = "0 45 * * * *")
    public void execute() {
        log.info("Batchen har startet (midlertidig infomelding)...");
        var antallLedige = organisasjonAdapter.hentAlleLedige().size();

        int manglende = OENSKET_ANTALL - antallLedige;
        if (manglende > 0) {
            log.info("Genererer {} nye orgnummer til databasen", manglende);
            orgnummerService.genererOrgnrsTilDb(manglende, true);
        }
    }

    @Scheduled(cron = "0 0 22 * * *")
    public void checkProd() {
        log.info("Batch checkProd har startet (midlertidig infomelding)...");
        var alle = organisasjonAdapter.hentAlleLedige();
        List<String> collect = alle.stream()
                .map(org -> orgnummerService.finnesOrgnr(org.getOrgnummer()) ? org.getOrgnummer() : null)
                .filter(Objects::nonNull).collect(Collectors.toList());

        if (collect.isEmpty()) {
            return;
        }
        collect.forEach(organisasjonAdapter::deleteByOrgnummer);
    }
}
