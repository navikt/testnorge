package no.nav.registre.orgnrservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import no.nav.registre.orgnrservice.adapter.OrganisasjonAdapter;

@Slf4j
@Component
@RequiredArgsConstructor
public class CronJobService {

    private static final int OENSKET_ANTALL = 5;
    private final OrgnummerService orgnummerService;
    private final OrganisasjonAdapter organisasjonAdapter;

    @Scheduled(cron = "0 0 20 * * *")
    public void execute() {
        var antallLedige = organisasjonAdapter.hentAlleLedige().size();

        int manglende = OENSKET_ANTALL - antallLedige;
        if (manglende > 0) {
            log.info("Genererer {} nye orgnummer til databasem", manglende);
            orgnummerService.generateOrgnrs(manglende);
        }
    }

    @Scheduled(cron = "0 0 22 * * *")
    public void checkProd() {
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
