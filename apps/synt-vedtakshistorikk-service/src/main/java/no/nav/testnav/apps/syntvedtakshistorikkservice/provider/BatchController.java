package no.nav.testnav.apps.syntvedtakshistorikkservice.provider;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.ArenaDagpengerService;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.ArenaForvalterService;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.IdentService;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.TagsService;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.VedtakshistorikkService;
import no.nav.testnav.libs.dto.dollysearchservice.v1.legacy.PersonDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MAKSIMUM_ALDER;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MINIMUM_ALDER;

@Slf4j
@Getter
@Component
@EnableScheduling
@RequiredArgsConstructor
public class BatchController {

    @Value("${batch.miljoe}")
    private String miljoe;

    @Value("${batch.antallMedHistorikk}")
    private int antallHistorikker;

    @Value("${batch.antallMedOppfoelging}")
    private int antallBrukere;

    @Value("${batch.antallMedDagpenger}")
    private int antallDagpenger;


    private final VedtakshistorikkService vedtakshistorikkService;
    private final IdentService identService;
    private final TagsService tagsService;
    private final ArenaForvalterService arenaForvalterService;
    private final ArenaDagpengerService arenaDagpengerService;

    /**
     * Denne metoden oppretter vedtakshistorikk på brukere i Arena. Metoden kjøres hver time.
     */
    @Scheduled(cron = "0 0 0-23 * * *")
    public void genererVedtakshistorikkBatch() {
        vedtakshistorikkService.genererVedtakshistorikk(miljoe, antallHistorikker);
    }

    /**
     * Denne metoden registrerer brukere med oppfølging (uten vedtak) i Arena. Metoden kjører hver natt kl 00:30.
     */
    @Scheduled(cron = "0 30 0 * * *")
    public void genererBrukereMedOppfoelgingBatch() {
        var personer = identService.getUtvalgteIdenterIAldersgruppe(
                        antallBrukere,
                        MINIMUM_ALDER,
                        MAKSIMUM_ALDER,
                        false
                );

        if (tagsService.opprettetTagsPaaIdenterOgPartner(personer)) {
            arenaForvalterService.opprettArbeidssoekereUtenVedtak(personer.stream().map(PersonDTO::getIdent).toList(), miljoe);
        }
    }

    /**
     * PÅ VENT: bytt fra forenklet versjon til versjon som oppretter dagpengesoknad og vedtak
     * når synt-dagpenger har fått trent på bedre uttrekk
     * Denne metoden oppretter dagpengevedtak i Arena. Metoden kjører hver natt kl 00:30.
     */
    @Scheduled(cron = "0 30 0 * * *")
    public void genererBrukereMedDagpengerBatch() {
        arenaDagpengerService.registrerArenaBrukereMedDagpenger(antallDagpenger, miljoe, true);
    }
}
