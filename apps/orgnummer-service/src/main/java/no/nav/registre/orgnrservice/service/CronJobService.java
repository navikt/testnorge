package no.nav.registre.orgnrservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orgnrservice.adapter.OrgnummerAdapter;
import no.nav.registre.orgnrservice.consumer.OrganisasjonConsumer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class CronJobService {

    private static final int OENSKET_ANTALL = 1000;
    private final OrgnummerService orgnummerService;
    private final OrganisasjonConsumer organisasjonConsumer;
    private final OrgnummerAdapter orgnummerAdapter;

    @Scheduled(cron = "0 0 20 * * *")
    public void execute() {
        var antallLedige = orgnummerAdapter.hentAlleLedige().block();

        int manglende = OENSKET_ANTALL - (antallLedige == null ? 0 : antallLedige.size());
        if (manglende > 0) {
            log.info("Genererer {} nye orgnummer til databasen", manglende);
            orgnummerService.genererOrgnrsTilDb(manglende, true)
                    .collectList()
                    .block();
        }
    }

    @Scheduled(cron = "0 0 22 * * *")
    public void checkMiljoe() {
        var alle = orgnummerAdapter.hentAlleLedige().block();
        if (alle == null) {
            return;
        }
        Flux.fromIterable(alle)
                .flatMap(org -> organisasjonConsumer.finnesOrgnrIEreg(org.getOrgnummer())
                        .filter(Boolean::booleanValue)
                        .flatMap(found -> orgnummerAdapter.deleteByOrgnummer(org.getOrgnummer())))
                .collectList()
                .block();
    }
}
