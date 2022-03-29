package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PdlProxyConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.RettighetRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.SyntVedtakshistorikkConsumer;

import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.Tags;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import org.springframework.stereotype.Service;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.finnSenesteVedtak;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.finnTidligsteDato;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.finnTidligsteDatoAap;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.finnTidligeDatoBarnetillegg;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MAX_ALDER_AAP;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MAX_ALDER_UNG_UFOER;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MIN_ALDER_AAP;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class VedtakshistorikkService {

    private final SyntVedtakshistorikkConsumer syntVedtakshistorikkConsumer;

    private final IdentService identService;
    private final PensjonService pensjonService;
    private final ArenaForvalterService arenaForvalterService;
    private final ArenaAapService arenaAapService;
    private final ArenaTiltakService arenaTiltakService;
    private final ArenaTilleggService arenaTilleggService;
    private final PdlProxyConsumer pdlProxyConsumer;
    private final ExecutorService dollyForkJoinPool;

    public static final List<Tags> SYNT_TAGS = Arrays.asList(Tags.DOLLY, Tags.ARENASYNT);


    public Map<String, List<NyttVedtakResponse>> genererVedtakshistorikk(
            String miljoe,
            int antallNyeIdenter
    ) {
        Map<String, List<NyttVedtakResponse>> responses = new HashMap<>();
        var intStream = IntStream.range(0, antallNyeIdenter).boxed().toList();
        try {
            dollyForkJoinPool.submit(() ->
                    intStream.parallelStream().forEach(i ->
                            opprettHistorikkForIdent(miljoe, responses)
                    )
            ).get();
        } catch (InterruptedException e) {
            log.error("Kunne ikke opprette vedtakshistorikk.", e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error("Kunne ikke opprette vedtakshistorikk.", e);
        } finally {
            dollyForkJoinPool.shutdown();
        }
        return responses;
    }

    private void opprettHistorikkForIdent(
            String miljoe,
            Map<String, List<NyttVedtakResponse>> responses
    ) {
        var vedtakshistorikkListe = syntVedtakshistorikkConsumer.syntetiserVedtakshistorikk(1);

        if (!vedtakshistorikkListe.isEmpty()) {
            var vedtakshistorikk = vedtakshistorikkListe.get(0);

            vedtakshistorikk.setTilsynFamiliemedlemmer(arenaTilleggService
                    .fjernTilsynFamiliemedlemmerVedtakMedUgyldigeDatoer(vedtakshistorikk.getTilsynFamiliemedlemmer()));
            vedtakshistorikk.setUngUfoer(arenaAapService.fjernAapUngUfoerMedUgyldigeDatoer(vedtakshistorikk.getUngUfoer()));
            arenaAapService.oppdaterAapSykepengeerstatningDatoer(vedtakshistorikk);

            LocalDate tidligsteDato = finnTidligsteDato(vedtakshistorikk);
            LocalDate tidligsteDatoBarnetillegg = finnTidligeDatoBarnetillegg(vedtakshistorikk.getBarnetillegg());

            if (isNull(tidligsteDato)) {
                return;
            }

            var minimumAlder = Math.toIntExact(ChronoUnit.YEARS.between(tidligsteDato.minusYears(MIN_ALDER_AAP), LocalDate.now()));
            var maksimumAlder = getMaksimumAlder(vedtakshistorikk, minimumAlder);

            if (minimumAlder > maksimumAlder) {
                log.error("Kunne ikke opprette vedtakshistorikk på ident med minimum alder {}.", minimumAlder);
            } else {
                var utvalgtIdent = getUtvalgtIdentIAldersgruppe(vedtakshistorikk, tidligsteDatoBarnetillegg, minimumAlder, maksimumAlder);
                if (nonNull(utvalgtIdent) && opprettetTagsPaaIdent(utvalgtIdent)) {
                    responses.putAll(opprettHistorikkOgSendTilArena(utvalgtIdent, miljoe, vedtakshistorikk, tidligsteDato));
                }
            }
        }
    }

    private int getMaksimumAlder(
            Vedtakshistorikk historikk,
            int minimumAlder
    ) {
        var maksimumAlder = minimumAlder + 50;
        if (maksimumAlder > MAX_ALDER_AAP) {
            maksimumAlder = MAX_ALDER_AAP;
        }

        var ungUfoer = historikk.getUngUfoer();
        if (nonNull(ungUfoer) && !ungUfoer.isEmpty()) {
            maksimumAlder = MAX_ALDER_UNG_UFOER;
        }
        return maksimumAlder;
    }

    private String getUtvalgtIdentIAldersgruppe(
            Vedtakshistorikk vedtakshistorikk,
            LocalDate tidligsteDatoBarnetillegg,
            int minimumAlder,
            int maksimumAlder
    ) {
        try {
            var aapVedtak = vedtakshistorikk.getAlleAapVedtak();
            var maaVaereBosatt = nonNull(aapVedtak) && !aapVedtak.isEmpty();
            LocalDate tidligsteDatoBosatt = maaVaereBosatt ? finnTidligsteDatoAap(aapVedtak) : null;

            List<String> identer;
            if (nonNull(tidligsteDatoBarnetillegg)) {
                identer = identService.getUtvalgteIdenterIAldersgruppeMedBarnUnder18(1, minimumAlder, maksimumAlder, tidligsteDatoBosatt, tidligsteDatoBarnetillegg);
            } else {
                identer = identService.getUtvalgteIdenterIAldersgruppe(1, minimumAlder, maksimumAlder, tidligsteDatoBosatt);
            }

            return identer.isEmpty() ? null : identer.get(0);

        } catch (RuntimeException e) {
            log.error("Kunne ikke hente utvalgt ident.", e);
            return null;
        }
    }

    private Map<String, List<NyttVedtakResponse>> opprettHistorikkOgSendTilArena(
            String personident,
            String miljoe,
            Vedtakshistorikk vedtakshistorikk,
            LocalDate tidligsteDato
    ) {
        List<RettighetRequest> rettigheter = new ArrayList<>();
        List<NyttVedtakTiltak> tiltak = new ArrayList<>();

        var ikkeAvluttendeAap115 = arenaAapService.getIkkeAvsluttendeVedtakAap115(vedtakshistorikk.getAap115());
        var avsluttendeAap115 = arenaAapService.getAvsluttendeVedtakAap115(vedtakshistorikk.getAap115());

        var senesteVedtak = finnSenesteVedtak(vedtakshistorikk.getAlleVedtak());

        arenaAapService.opprettVedtakAap115(ikkeAvluttendeAap115, personident, miljoe, rettigheter);
        arenaAapService.opprettVedtakAap(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaAapService.opprettVedtakAap115(avsluttendeAap115, personident, miljoe, rettigheter);
        arenaAapService.opprettVedtakUngUfoer(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaAapService.opprettVedtakTvungenForvaltning(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaAapService.opprettVedtakFritakMeldekort(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaTiltakService.oppdaterTiltaksdeltakelse(vedtakshistorikk, personident, miljoe, tiltak, senesteVedtak, tidligsteDato);
        arenaTiltakService.opprettVedtakTiltaksdeltakelse(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaTiltakService.opprettFoersteVedtakEndreDeltakerstatus(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaTiltakService.opprettVedtakTiltakspenger(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaTiltakService.opprettVedtakBarnetillegg(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaTiltakService.opprettAvsluttendeVedtakEndreDeltakerstatus(vedtakshistorikk, personident, miljoe, rettigheter, tiltak);
        arenaTilleggService.opprettVedtakTillegg(vedtakshistorikk, personident, miljoe, rettigheter, tiltak);

        if (!rettigheter.isEmpty()) {
            if (!opprettetNoedvendigInfoIPopp(vedtakshistorikk, personident, miljoe)) {
                removeTagsPaaIdent(personident);
                slettIdentIArena(personident, miljoe);
                return Collections.emptyMap();
            }
            try {
                arenaForvalterService.opprettArbeidssoekerVedtakshistorikk(personident, miljoe, senesteVedtak, tidligsteDato);
            } catch (Exception e) {
                log.error(e.getMessage());
                return Collections.emptyMap();
            }
            return arenaForvalterService.opprettRettigheterIArena(rettigheter);
        } else {
            return Collections.emptyMap();
        }
    }

    private boolean opprettetNoedvendigInfoIPopp(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe
    ) {
        var aap = historikk.getAap();
        var aap115 = historikk.getAap115();
        if (nonNull(aap) && !aap.isEmpty()) {
            return pensjonService.opprettetPersonOgInntektIPopp(personident, miljoe, aap.get(0).getFraDato());
        } else if (nonNull(aap115) && !aap115.isEmpty()) {
            return pensjonService.opprettetPersonOgInntektIPopp(personident, miljoe, aap115.get(0).getFraDato());
        }
        return true;
    }

    private boolean opprettetTagsPaaIdent(String ident) {
        return pdlProxyConsumer.createTags(Collections.singletonList(ident), SYNT_TAGS);
    }

    private void removeTagsPaaIdent(String ident) {
        pdlProxyConsumer.deleteTags(Collections.singletonList(ident), SYNT_TAGS);
    }

    private void slettIdentIArena(String ident, String miljoe) {
        arenaForvalterService.slettArbeidssoekerIArena(ident, miljoe);
    }

}
