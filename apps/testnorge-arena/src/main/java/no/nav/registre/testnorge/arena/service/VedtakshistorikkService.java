package no.nav.registre.testnorge.arena.service;

import static no.nav.registre.testnorge.arena.service.util.RequestUtils.getRettighetFinnTiltakRequest;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.ARENA_AAP_UNG_UFOER_DATE_LIMIT;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.SYKEPENGEERSTATNING_MAKS_PERIODE;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.AKTIVITETSFASE_SYKEPENGEERSTATNING;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MAX_ALDER_AAP;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MAX_ALDER_UNG_UFOER;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MIN_ALDER_AAP;

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

import no.nav.registre.testnorge.arena.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetRequest;
import no.nav.registre.testnorge.arena.consumer.rs.SyntVedtakshistorikkConsumer;
import no.nav.registre.testnorge.arena.service.util.RequestUtils;
import no.nav.registre.testnorge.arena.service.util.TilleggUtils;
import no.nav.registre.testnorge.arena.service.util.TiltakUtils;

import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Deltakerstatuser;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import no.nav.testnav.libs.servletcore.util.IdentUtil;

import org.springframework.stereotype.Service;

import static no.nav.registre.testnorge.arena.service.util.RequestUtils.getRettighetAap115Request;
import static no.nav.registre.testnorge.arena.service.util.RequestUtils.getRettighetAapRequest;
import static no.nav.registre.testnorge.arena.service.util.RequestUtils.getRettighetFritakMeldekortRequest;
import static no.nav.registre.testnorge.arena.service.util.RequestUtils.getRettighetTilleggRequest;
import static no.nav.registre.testnorge.arena.service.util.RequestUtils.getRettighetTilleggsytelseRequest;
import static no.nav.registre.testnorge.arena.service.util.RequestUtils.getRettighetTiltaksdeltakelseRequest;
import static no.nav.registre.testnorge.arena.service.util.RequestUtils.getRettighetTiltakspengerRequest;
import static no.nav.registre.testnorge.arena.service.util.RequestUtils.getRettighetTvungenForvaltningRequest;
import static no.nav.registre.testnorge.arena.service.util.RequestUtils.getRettighetUngUfoerRequest;

import static no.nav.registre.testnorge.arena.service.util.DatoUtils.finnSenesteVedtak;
import static no.nav.registre.testnorge.arena.service.util.DatoUtils.finnTidligsteDato;
import static no.nav.registre.testnorge.arena.service.util.DatoUtils.finnTidligsteDatoAap;
import static no.nav.registre.testnorge.arena.service.util.DatoUtils.finnTidligeDatoBarnetillegg;
import static no.nav.registre.testnorge.arena.service.util.DatoUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder;
import static no.nav.registre.testnorge.arena.service.util.VedtakUtils.getTilleggSekvenser;

@Slf4j
@Service
@RequiredArgsConstructor
public class VedtakshistorikkService {

    private final SyntVedtakshistorikkConsumer syntVedtakshistorikkConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;

    private final IdentService identService;
    private final PensjonService pensjonService;
    private final ArenaBrukerService arenaBrukerService;

    private final RequestUtils requestUtils;
    private final TilleggUtils tilleggUtils;
    private final TiltakUtils tiltakUtils;

    private static final String MAALGRUPPEKODE_TILKNYTTET_AAP = "NEDSARBEVN";
    private static final String MAALGRUPPEKODE_TILKNYTTET_TILTAKSPENGER = "MOTTILTPEN";
    private static final LocalDate ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT = LocalDate.of(2020, 2, 29);

    public Map<String, List<NyttVedtakResponse>> genererVedtakshistorikk(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        Map<String, List<NyttVedtakResponse>> responses = new HashMap<>();
        var intStream = IntStream.range(0, antallNyeIdenter).boxed().collect(Collectors.toList());
        var forkJoinPool = new ForkJoinPool(10);
        try {
            forkJoinPool.submit(() ->
                    intStream.parallelStream().forEach(i ->
                            opprettHistorikkForIdent(avspillergruppeId, miljoe, responses)
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
            Long avspillergruppeId,
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
                var utvalgtIdent = getUtvalgtIdentIAldersgruppe(avspillergruppeId, miljoe, vedtakshistorikk, tidligsteDatoBarnetillegg, minimumAlder, maksimumAlder);
                if (utvalgtIdent != null) {
                    responses.putAll(opprettHistorikkOgSendTilArena(avspillergruppeId, utvalgtIdent, miljoe, vedtakshistorikk, tidligsteDato));
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
            Long avspillergruppeId,
            String miljoe,
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
                identer = identService.getUtvalgteIdenterIAldersgruppeMedBarnUnder18(avspillergruppeId, 1, minimumAlder, maksimumAlder, miljoe, tidligsteDatoBosatt, tidligsteDatoBarnetillegg);
            } else {
                identer = identService.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, 1, minimumAlder, maksimumAlder, miljoe, tidligsteDatoBosatt);
            }

            return identer.isEmpty() ? null : identer.get(0);

        } catch (RuntimeException e) {
            log.error("Kunne ikke hente utvalgt ident.", e);
            return null;
        }
    }

    private Map<String, List<NyttVedtakResponse>> opprettHistorikkOgSendTilArena(
            Long avspillergruppeId,
            String personident,
            String miljoe,
            Vedtakshistorikk vedtakshistorikk,
            LocalDate tidligsteDato
    ) {
        List<KontoinfoResponse> identerMedKontonummer = new ArrayList<>();
        if (vedtakshistorikk.getTvungenForvaltning() != null && !vedtakshistorikk.getTvungenForvaltning().isEmpty()) {
            var antallTvungenForvaltning = vedtakshistorikk.getTvungenForvaltning().size();
            identerMedKontonummer = identService.getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallTvungenForvaltning);
        }

        List<RettighetRequest> rettigheter = new ArrayList<>();
        List<NyttVedtakTiltak> tiltak = new ArrayList<>();

        var ikkeAvluttendeAap115 = getIkkeAvsluttendeVedtakAap115(vedtakshistorikk.getAap115());
        var avsluttendeAap115 = getAvsluttendeVedtakAap115(vedtakshistorikk.getAap115());

        if (!opprettetNoedvendigInfoIPopp(vedtakshistorikk, personident, miljoe)) {
            return Collections.emptyMap();
        }

        var senesteVedtak = finnSenesteVedtak(vedtakshistorikk.getAlleVedtak());

        opprettVedtakAap115(ikkeAvluttendeAap115, personident, miljoe, rettigheter);
        opprettVedtakAap(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakAap115(avsluttendeAap115, personident, miljoe, rettigheter);
        opprettVedtakUngUfoer(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakTvungenForvaltning(vedtakshistorikk, personident, miljoe, rettigheter, identerMedKontonummer);
        opprettVedtakFritakMeldekort(vedtakshistorikk, personident, miljoe, rettigheter);
        oppdaterTiltaksdeltakelse(vedtakshistorikk, personident, miljoe, tiltak, senesteVedtak, tidligsteDato);
        opprettVedtakTiltaksdeltakelse(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettFoersteVedtakEndreDeltakerstatus(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakTiltakspenger(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakBarnetillegg(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettAvsluttendeVedtakEndreDeltakerstatus(vedtakshistorikk, personident, miljoe, rettigheter, tiltak);
        opprettVedtakTillegg(vedtakshistorikk, personident, miljoe, rettigheter, tiltak);

        if (!rettigheter.isEmpty()) {
            try {
                arenaBrukerService.opprettArbeidssoekerVedtakshistorikk(personident, miljoe, senesteVedtak, tidligsteDato);
            } catch (Exception e) {
                log.error(e.getMessage());
                return Collections.emptyMap();
            }
            return rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);
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

    private List<NyttVedtakAap> getIkkeAvsluttendeVedtakAap115(
            List<NyttVedtakAap> aap115
    ) {
        List<NyttVedtakAap> vedtaksliste = new ArrayList<>();
        if (aap115 != null && !aap115.isEmpty()) {
            vedtaksliste = aap115.stream().filter(vedtak -> !vedtak.getVedtaktype().equals("S"))
                    .collect(Collectors.toList());
        }
        return vedtaksliste;
    }

    private List<NyttVedtakAap> getAvsluttendeVedtakAap115(
            List<NyttVedtakAap> aap115
    ) {
        List<NyttVedtakAap> vedtaksliste = new ArrayList<>();
        if (aap115 != null && !aap115.isEmpty()) {
            vedtaksliste = aap115.stream().filter(vedtak -> vedtak.getVedtaktype().equals("S"))
                    .collect(Collectors.toList());
        }
        return vedtaksliste;
    }

    private void opprettVedtakAap115(
            List<NyttVedtakAap> aap115,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        if (aap115 != null && !aap115.isEmpty()) {
            for (var vedtak : aap115) {
                rettigheter.add(getRettighetAap115Request(personident, miljoe, vedtak));
            }
        }
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

    private void opprettVedtakAap(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var aap = historikk.getAap();
        if (aap != null && !aap.isEmpty()) {
            for (var vedtak : aap) {
                rettigheter.add(getRettighetAapRequest(personident, miljoe, vedtak));
            }

        }
    }

    private void opprettVedtakUngUfoer(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var foedselsdato = IdentUtil.getFoedselsdatoFraIdent(personident);
        var ungUfoer = historikk.getUngUfoer();
        if (ungUfoer != null && !ungUfoer.isEmpty()) {
            for (var vedtak : ungUfoer) {
                var rettighetRequest = getRettighetUngUfoerRequest(personident, miljoe, foedselsdato, vedtak);

                if (rettighetRequest != null) {
                    rettigheter.add(rettighetRequest);
                }
            }

        }
    }

    private void opprettVedtakTvungenForvaltning(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter,
            List<KontoinfoResponse> identerMedKontonummer
    ) {
        var tvungenForvaltning = historikk.getTvungenForvaltning();
        if (tvungenForvaltning != null && !tvungenForvaltning.isEmpty()) {
            for (var vedtak : tvungenForvaltning) {
                var rettighetRequest = getRettighetTvungenForvaltningRequest(
                        personident,
                        miljoe,
                        identerMedKontonummer.remove(identerMedKontonummer.size() - 1),
                        vedtak);
                rettigheter.add(rettighetRequest);
            }
        }
    }

    private void opprettVedtakFritakMeldekort(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var fritakMeldekort = historikk.getFritakMeldekort();
        if (fritakMeldekort != null && !fritakMeldekort.isEmpty()) {
            for (var vedtak : fritakMeldekort) {
                rettigheter.add(getRettighetFritakMeldekortRequest(personident, miljoe, vedtak));
            }
        }
    }

    private void oppdaterTiltaksdeltakelse(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<NyttVedtakTiltak> tiltaksliste,
            NyttVedtak senesteVedtak,
            LocalDate tidligsteDato
    ) {
        var tiltaksdeltakelser = historikk.getTiltaksdeltakelse();
        if (tiltaksdeltakelser != null && !tiltaksdeltakelser.isEmpty()) {
            Kvalifiseringsgrupper kvalifiseringsgruppe;
            try {
                kvalifiseringsgruppe = arenaBrukerService.opprettArbeidssoekerTiltaksdeltakelse(personident, miljoe, senesteVedtak.getRettighetType(), tidligsteDato);
            } catch (Exception e) {
                historikk.setTiltaksdeltakelse(Collections.emptyList());
                return;
            }

            tiltaksdeltakelser.forEach(deltakelse -> {
                if (tiltakUtils.harIkkeGyldigTiltakKode(deltakelse, kvalifiseringsgruppe)) {
                    deltakelse.setTiltakKode(tiltakUtils.getGyldigTiltakKode(deltakelse, kvalifiseringsgruppe));
                }
                deltakelse.setFodselsnr(personident);
                deltakelse.setTiltakYtelse("J");
            });
            tiltaksdeltakelser.forEach(deltakelse -> {
                var tiltak = finnTiltak(personident, miljoe, deltakelse);

                if (tiltak != null) {
                    deltakelse.setTiltakId(tiltak.getTiltakId());
                    deltakelse.setTiltakProsentDeltid(tiltak.getTiltakProsentDeltid());
                    deltakelse.setFraDato(tiltak.getFraDato());
                    deltakelse.setTilDato(tiltak.getTilDato());
                    deltakelse.setTiltakYtelse(tiltak.getTiltakYtelse());
                    tiltaksliste.add(tiltak);
                }
            });

            var nyeTiltaksdeltakelser = tiltaksdeltakelser.stream()
                    .filter(deltakelse -> deltakelse.getTiltakId() != null).collect(Collectors.toList());

            nyeTiltaksdeltakelser = tiltakUtils.removeOverlappingTiltakVedtak(nyeTiltaksdeltakelser, historikk.getAap());

            historikk.setTiltaksdeltakelse(nyeTiltaksdeltakelser);
        }
    }

    private NyttVedtakTiltak finnTiltak(String personident, String miljoe, NyttVedtakTiltak tiltaksdeltakelse) {
        var response = rettighetArenaForvalterConsumer.finnTiltak(getRettighetFinnTiltakRequest(personident, miljoe, tiltaksdeltakelse));
        if (response != null && !response.getNyeRettigheterTiltak().isEmpty()) {
            return response.getNyeRettigheterTiltak().get(0);
        } else {
            log.info("Fant ikke tiltak for tiltakdeltakelse.");
            return null;
        }
    }

    private void opprettVedtakTiltaksdeltakelse(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var tiltaksdeltakelser = historikk.getTiltaksdeltakelse();
        if (tiltaksdeltakelser != null && !tiltaksdeltakelser.isEmpty()) {
            for (var deltakelse : tiltaksdeltakelser) {
                rettigheter.add(getRettighetTiltaksdeltakelseRequest(personident, miljoe, deltakelse));
            }
        }
    }

    private void opprettFoersteVedtakEndreDeltakerstatus(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var tiltaksdeltakelser = historikk.getTiltaksdeltakelse();

        var nyeTiltaksedeltakelser = new ArrayList<NyttVedtakTiltak>();

        if (tiltaksdeltakelser == null || tiltaksdeltakelser.isEmpty()) {
            return;
        }
        for (var deltakelse : tiltaksdeltakelser) {
            if (tiltakUtils.canSetDeltakelseTilGjennomfoeres(deltakelse)) {
                List<String> endringer = tiltakUtils.getFoersteEndringerDeltakerstatus(deltakelse.getTiltakAdminKode());

                for (var endring : endringer) {
                    var rettighetRequest = requestUtils.getRettighetEndreDeltakerstatusRequest(personident, miljoe,
                            deltakelse, endring);

                    rettigheter.add(rettighetRequest);
                }

                // Hvis siste endring ikke er lik GJENN kan ikke andre tiltak-vedtak (BASI/BTIL) knyttes til tiltaksdeltakelsen.
                if (!endringer.isEmpty() && endringer.get(endringer.size() - 1).equals(Deltakerstatuser.GJENN.toString())) {
                    nyeTiltaksedeltakelser.add(deltakelse);
                }
            }
        }

        historikk.setTiltaksdeltakelse(nyeTiltaksedeltakelser);
    }

    private void opprettAvsluttendeVedtakEndreDeltakerstatus(
            Vedtakshistorikk vedtak,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter,
            List<NyttVedtakTiltak> tiltak
    ) {
        var tiltaksdeltakelser = vedtak.getTiltaksdeltakelse();
        if (tiltaksdeltakelser != null && !tiltaksdeltakelser.isEmpty()) {
            for (var deltakelse : tiltaksdeltakelser) {
                if (tiltakUtils.canSetDeltakelseTilFinished(deltakelse, tiltak)) {
                    var deltakerstatuskode = tiltakUtils.getAvsluttendeDeltakerstatus(deltakelse, tiltak).toString();

                    var rettighetRequest = requestUtils.getRettighetEndreDeltakerstatusRequest(personident, miljoe,
                            deltakelse, deltakerstatuskode);

                    rettigheter.add(rettighetRequest);
                }
            }
        }
    }

    private void opprettVedtakTiltakspenger(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var tiltakspenger = historikk.getTiltakspenger() != null ? historikk.getTiltakspenger() : new ArrayList<NyttVedtakTiltak>();
        var tiltaksdeltakelser = historikk.getTiltaksdeltakelse();

        List<NyttVedtakTiltak> nyeTiltakspenger = tiltakUtils.oppdaterVedtakslisteBasertPaaTiltaksdeltakelse(
                tiltakspenger, tiltaksdeltakelser);

        nyeTiltakspenger = tiltakUtils.removeOverlappingTiltakSequences(nyeTiltakspenger);

        if (nyeTiltakspenger != null && !nyeTiltakspenger.isEmpty()) {
            for (var vedtak : nyeTiltakspenger) {
                rettigheter.add(getRettighetTiltakspengerRequest(personident, miljoe, vedtak));
            }
        }
        historikk.setTiltakspenger(nyeTiltakspenger);
    }

    private void opprettVedtakBarnetillegg(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var barnetillegg = historikk.getBarnetillegg() != null ? historikk.getBarnetillegg() : new ArrayList<NyttVedtakTiltak>();
        var tiltakspenger = historikk.getTiltakspenger() != null ? historikk.getTiltakspenger() : new ArrayList<NyttVedtakTiltak>();
        var tiltaksdeltakelser = historikk.getTiltaksdeltakelse();

        List<NyttVedtakTiltak> nyeBarnetillegg = new ArrayList<>();
        if (!tiltakspenger.isEmpty()) {
            nyeBarnetillegg = tiltakUtils.oppdaterVedtakslisteBasertPaaTiltaksdeltakelse(
                    barnetillegg, tiltaksdeltakelser);

            nyeBarnetillegg = tiltakUtils.removeOverlappingTiltakSequences(nyeBarnetillegg);

            if (nyeBarnetillegg != null && !nyeBarnetillegg.isEmpty()) {
                for (var vedtak : nyeBarnetillegg) {
                    rettigheter.add(getRettighetTilleggsytelseRequest(personident, miljoe, vedtak));
                }
            }
        }

        historikk.setBarnetillegg(nyeBarnetillegg);
    }

    private void opprettVedtakTillegg(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter,
            List<NyttVedtakTiltak> tiltak
    ) {
        var tillegg = oppdaterVedtakTillegg(historikk);

        if (tillegg != null && !tillegg.isEmpty() && !rettigheter.isEmpty()) {
            var tilleggSekvenser = getTilleggSekvenser(tillegg);

            for (var sekvens : tilleggSekvenser) {
                if (tilleggUtils.tilleggSekvensManglerTiltak(sekvens, tiltak)) {
                    rettigheter.add(tilleggUtils.getTiltaksaktivitetForTilleggSekvens(personident, miljoe, sekvens));
                }

                for (var vedtak : sekvens) {
                    rettigheter.add(getRettighetTilleggRequest(personident, miljoe, vedtak));
                }
            }
        }
    }

    private List<NyttVedtakTillegg> oppdaterVedtakTillegg(Vedtakshistorikk historikk) {
        var tillegg = historikk.getAlleTilleggVedtak();

        if (historikk.getAap() == null || historikk.getAap().isEmpty()) {
            tillegg = tilleggUtils.filtrerBortTilleggMedUoensketMaalgruppekode(tillegg, MAALGRUPPEKODE_TILKNYTTET_AAP);
        } else {
            tillegg = tilleggUtils.filtrerBortTilleggUtenGyldigTilknyttetVedtak(tillegg, historikk.getAap(),
                    MAALGRUPPEKODE_TILKNYTTET_AAP);
        }

        if (historikk.getTiltakspenger() == null || historikk.getTiltakspenger().isEmpty()) {
            tillegg = tilleggUtils.filtrerBortTilleggMedUoensketMaalgruppekode(tillegg, MAALGRUPPEKODE_TILKNYTTET_TILTAKSPENGER);
        } else {
            tillegg = tilleggUtils.filtrerBortTilleggUtenGyldigTilknyttetVedtak(tillegg, historikk.getTiltakspenger(),
                    MAALGRUPPEKODE_TILKNYTTET_TILTAKSPENGER);
        }
        return tillegg;

    }

}
