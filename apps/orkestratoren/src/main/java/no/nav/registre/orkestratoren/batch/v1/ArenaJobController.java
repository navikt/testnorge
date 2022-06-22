package no.nav.registre.orkestratoren.batch.v1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.orkestratoren.service.ArenaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ArenaJobController {

    @Value("${batch.miljoe}")
    private String miljoe;

    @Value("${batch.arena.antallHistorikker}")
    private int antallHistorikker;

    @Value("${batch.arena.antallBrukere}")
    private int antallBrukere;

    @Value("${batch.arena.antallDagpenger}")
    private int antallDagpenger;

    private final ArenaService arenaService;

    /**
     * Denne metoden oppretter vedtakshistorikk i Arena og registerer brukere med oppfølging (uten vedtak) i Arena.
     * Metoden kjøres hver time.
     */
    @Scheduled(cron = "0 0 0-23 * * *")
    public void arenaSyntVedtakshistorikkBatch() {
        arenaService.opprettArenaVedtakshistorikk(SyntetiserArenaRequest.builder()
                .miljoe(miljoe)
                .antallNyeIdenter(antallHistorikker)
                .build());
        arenaService.opprettArbeidssoekereMedOppfoelgingIArena(SyntetiserArenaRequest.builder()
                .miljoe(miljoe)
                .antallNyeIdenter(1)
                .build());

    }

    /**
     * Denne metoden registrerer brukere med oppfølging (uten vedtak) i Arena. Metoden kjører hver natt ved midnatt.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void arenaSyntBrukerBatch() {
        arenaService.opprettArbeidssoekereMedOppfoelgingIArena(SyntetiserArenaRequest.builder()
                .miljoe(miljoe)
                .antallNyeIdenter(antallBrukere)
                .build());

    }

    /**
     * Denne metoden oppretter dagpengesoknad og vedtak i Arena. Metoden kjører hver natt ved midnatt.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void arenaSyntDagpengerBatch() {
        arenaService.opprettDagpengerArena(SyntetiserArenaRequest.builder()
                .miljoe(miljoe)
                .antallNyeIdenter(antallDagpenger)
                .build());

    }
}
