package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.ARENA_AAP_UNG_UFOER_DATE_LIMIT;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.SYKEPENGEERSTATNING_MAKS_PERIODE;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.AKTIVITETSFASE_SYKEPENGEERSTATNING;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MAX_ALDER_AAP;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MAX_ALDER_UNG_UFOER;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MIN_ALDER_AAP;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.SyntVedtakshistorikkConsumer;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.KontoinfoResponse;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import org.springframework.stereotype.Service;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.finnSenesteVedtak;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.finnTidligsteDato;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.finnTidligsteDatoAap;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.finnTidligeDatoBarnetillegg;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder;

@Slf4j
@Service
@RequiredArgsConstructor
public class VedtakshistorikkService {

    private final SyntVedtakshistorikkConsumer syntVedtakshistorikkConsumer;

    private final IdentService identService;
    private final PensjonService pensjonService;
    private final ArenaForvalterService arenaForvalterService;
    private final ArenaTiltakService arenaTiltakService;
    private final ArenaTilleggService arenaTilleggService;
    private final ArenaAapService arenaAapService;

    private static final LocalDate ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT = LocalDate.of(2020, 2, 29);

    public Map<String, List<NyttVedtakResponse>> genererVedtakshistorikk(
            String miljoe,
            int antallNyeIdenter
    ) {
        Map<String, List<NyttVedtakResponse>> responses = new HashMap<>();
        var intStream = IntStream.range(0, antallNyeIdenter).boxed().collect(Collectors.toList());
        var forkJoinPool = new ForkJoinPool(10);
        try {
            forkJoinPool.submit(() ->
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
            forkJoinPool.shutdown();
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

            vedtakshistorikk.setTilsynFamiliemedlemmer(fjernTilsynFamiliemedlemmerVedtakMedUgyldigeDatoer(vedtakshistorikk.getTilsynFamiliemedlemmer()));
            vedtakshistorikk.setUngUfoer(fjernAapUngUfoerMedUgyldigeDatoer(vedtakshistorikk.getUngUfoer()));
            oppdaterAapSykepengeerstatningDatoer(vedtakshistorikk.getAap());

            LocalDate tidligsteDato = finnTidligsteDato(vedtakshistorikk);
            LocalDate tidligsteDatoBarnetillegg = finnTidligeDatoBarnetillegg(vedtakshistorikk.getBarnetillegg());

            if (tidligsteDato == null) {
                return;
            }

            var minimumAlder = Math.toIntExact(ChronoUnit.YEARS.between(tidligsteDato.minusYears(MIN_ALDER_AAP), LocalDate.now()));
            var maksimumAlder = getMaksimumAlder(vedtakshistorikk, minimumAlder);

            if (minimumAlder > maksimumAlder) {
                log.error("Kunne ikke opprette vedtakshistorikk på ident med minimum alder {}.", minimumAlder);
            } else {
                var utvalgtIdent = getUtvalgtIdentIAldersgruppe(vedtakshistorikk, tidligsteDatoBarnetillegg, minimumAlder, maksimumAlder);
                if (utvalgtIdent != null) {
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
        if (ungUfoer != null && !ungUfoer.isEmpty()) {
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
            var maaVaereBosatt = aapVedtak != null && !aapVedtak.isEmpty();
            LocalDate tidligsteDatoBosatt = maaVaereBosatt ? finnTidligsteDatoAap(aapVedtak) : null;

            List<String> identer;
            if (tidligsteDatoBarnetillegg != null) {
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
        List<KontoinfoResponse> identerMedKontonummer = new ArrayList<>();
        if (vedtakshistorikk.getTvungenForvaltning() != null && !vedtakshistorikk.getTvungenForvaltning().isEmpty()) {
            var antallTvungenForvaltning = vedtakshistorikk.getTvungenForvaltning().size();
            identerMedKontonummer = identService.getIdenterMedKontoinformasjon(antallTvungenForvaltning);
        }

        List<RettighetRequest> rettigheter = new ArrayList<>();
        List<NyttVedtakTiltak> tiltak = new ArrayList<>();

        var ikkeAvluttendeAap115 = arenaAapService.getIkkeAvsluttendeVedtakAap115(vedtakshistorikk.getAap115());
        var avsluttendeAap115 = arenaAapService.getAvsluttendeVedtakAap115(vedtakshistorikk.getAap115());

        if (!opprettetNoedvendigInfoIPopp(vedtakshistorikk, personident, miljoe)) {
            return Collections.emptyMap();
        }

        var senesteVedtak = finnSenesteVedtak(vedtakshistorikk.getAlleVedtak());

        arenaAapService.opprettVedtakAap115(ikkeAvluttendeAap115, personident, miljoe, rettigheter);
        arenaAapService.opprettVedtakAap(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaAapService.opprettVedtakAap115(avsluttendeAap115, personident, miljoe, rettigheter);
        arenaAapService.opprettVedtakUngUfoer(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaAapService.opprettVedtakTvungenForvaltning(vedtakshistorikk, personident, miljoe, rettigheter, identerMedKontonummer);
        arenaAapService.opprettVedtakFritakMeldekort(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaTiltakService.oppdaterTiltaksdeltakelse(vedtakshistorikk, personident, miljoe, tiltak, senesteVedtak, tidligsteDato);
        arenaTiltakService.opprettVedtakTiltaksdeltakelse(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaTiltakService.opprettFoersteVedtakEndreDeltakerstatus(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaTiltakService.opprettVedtakTiltakspenger(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaTiltakService.opprettVedtakBarnetillegg(vedtakshistorikk, personident, miljoe, rettigheter);
        arenaTiltakService.opprettAvsluttendeVedtakEndreDeltakerstatus(vedtakshistorikk, personident, miljoe, rettigheter, tiltak);
        arenaTilleggService.opprettVedtakTillegg(vedtakshistorikk, personident, miljoe, rettigheter, tiltak);

        if (!rettigheter.isEmpty()) {
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

    private void oppdaterAapSykepengeerstatningDatoer(List<NyttVedtakAap> aapVedtak) {
        if (aapVedtak != null) {
            var antallDagerEndret = 0;
            for (var vedtak : aapVedtak) {
                if (AKTIVITETSFASE_SYKEPENGEERSTATNING.equals(vedtak.getAktivitetsfase()) && vedtak.getFraDato() != null) {
                    vedtak.setFraDato(vedtak.getFraDato().minusDays(antallDagerEndret));
                    if (vedtak.getTilDato() == null) {
                        vedtak.setTilDato(vedtak.getFraDato().plusMonths(6));
                    } else {
                        vedtak.setTilDato(vedtak.getTilDato().minusDays(antallDagerEndret));

                        var originalTilDato = vedtak.getTilDato();
                        setDatoPeriodeVedtakInnenforMaxAntallMaaneder(vedtak, SYKEPENGEERSTATNING_MAKS_PERIODE);
                        var nyTilDato = vedtak.getTilDato();

                        antallDagerEndret += ChronoUnit.DAYS.between(nyTilDato, originalTilDato);
                    }
                }
            }
        }
    }

    private List<NyttVedtakTillegg> fjernTilsynFamiliemedlemmerVedtakMedUgyldigeDatoer(List<NyttVedtakTillegg> tilsynFamiliemedlemmer) {
        List<NyttVedtakTillegg> nyTilsynFamiliemedlemmer = new ArrayList<>();
        if (tilsynFamiliemedlemmer != null) {
            nyTilsynFamiliemedlemmer = tilsynFamiliemedlemmer.stream().filter(vedtak ->
                            !vedtak.getFraDato().isAfter(ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT))
                    .collect(Collectors.toList());
        }

        return nyTilsynFamiliemedlemmer.isEmpty() ? null : nyTilsynFamiliemedlemmer;
    }

    private List<NyttVedtakAap> fjernAapUngUfoerMedUgyldigeDatoer(List<NyttVedtakAap> ungUfoer) {
        List<NyttVedtakAap> nyUngUfoer = new ArrayList<>();
        if (ungUfoer != null) {
            nyUngUfoer = ungUfoer.stream().filter(vedtak ->
                            !vedtak.getFraDato().isAfter(ARENA_AAP_UNG_UFOER_DATE_LIMIT))
                    .collect(Collectors.toList());
        }

        return nyUngUfoer.isEmpty() ? null : nyUngUfoer;
    }

    private boolean opprettetNoedvendigInfoIPopp(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe
    ) {
        var aap = historikk.getAap();
        var aap115 = historikk.getAap115();
        if (aap != null && !aap.isEmpty()) {
            return pensjonService.opprettetPersonOgInntektIPopp(personident, miljoe, aap.get(0).getFraDato());
        } else if (aap115 != null && !aap115.isEmpty()) {
            return pensjonService.opprettetPersonOgInntektIPopp(personident, miljoe, aap115.get(0).getFraDato());
        }
        return true;
    }

}
