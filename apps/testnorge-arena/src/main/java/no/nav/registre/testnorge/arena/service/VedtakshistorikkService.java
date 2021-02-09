package no.nav.registre.testnorge.arena.service;

import static no.nav.registre.testnorge.arena.service.RettighetAapService.ARENA_AAP_UNG_UFOER_DATE_LIMIT;
import static no.nav.registre.testnorge.arena.service.RettighetTilleggService.ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT;
import static no.nav.registre.testnorge.arena.service.RettighetAapService.SYKEPENGEERSTATNING_MAKS_PERIODE;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.AKTIVITETSFASE_SYKEPENGEERSTATNING;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.BEGRUNNELSE;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MAX_ALDER_AAP;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MAX_ALDER_UNG_UFOER;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MIN_ALDER_AAP;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MIN_ALDER_UNG_UFOER;

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
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetAap115Request;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetAapRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetFritakMeldekortRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTilleggRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTilleggsytelseRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTiltaksdeltakelseRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTiltakspengerRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.testnorge.arena.consumer.rs.VedtakshistorikkSyntConsumer;
import no.nav.registre.testnorge.arena.service.exception.VedtakshistorikkException;
import no.nav.registre.testnorge.arena.service.util.ArbeidssoekerUtils;
import no.nav.registre.testnorge.arena.service.util.DatoUtils;
import no.nav.registre.testnorge.arena.service.util.IdenterUtils;
import no.nav.registre.testnorge.arena.service.util.ServiceUtils;
import no.nav.registre.testnorge.arena.service.util.VedtakUtils;

import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Deltakerstatuser;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import no.nav.registre.testnorge.libs.core.util.IdentUtil;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VedtakshistorikkService {

    private final VedtakshistorikkSyntConsumer vedtakshistorikkSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final IdenterUtils identerUtils;
    private final ArbeidssoekerUtils arbeidsoekerUtils;
    private final VedtakUtils vedtakUtils;
    private final RettighetAapService rettighetAapService;
    private final RettighetTiltakService rettighetTiltakService;
    private final DatoUtils datoUtils;

    public static final String MAALGRUPPEKODE_TILKNYTTET_AAP = "NEDSARBEVN";
    public static final String MAALGRUPPEKODE_TILKNYTTET_TILTAKSPENGER = "MOTTILTPEN";

    public Map<String, List<NyttVedtakResponse>> genererVedtakshistorikk(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        Map<String, List<NyttVedtakResponse>> responses = new HashMap<>();
        var intStream = IntStream.range(0, antallNyeIdenter).boxed().collect(Collectors.toList());
        ForkJoinPool forkJoinPool = null;
        try {
            forkJoinPool = new ForkJoinPool(10);
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
            if (forkJoinPool != null) {
                forkJoinPool.shutdown();
            }
        }
        return responses;
    }

    private void opprettHistorikkForIdent(
            Long avspillergruppeId,
            String miljoe,
            Map<String, List<NyttVedtakResponse>> responses
    ) {
        var vedtakshistorikk = vedtakshistorikkSyntConsumer.syntetiserVedtakshistorikk(1);
        for (var vedtakshistorikken : vedtakshistorikk) {
            vedtakshistorikken.setTilsynFamiliemedlemmer(fjernTilsynFamiliemedlemmerVedtakMedUgyldigeDatoer(vedtakshistorikken.getTilsynFamiliemedlemmer()));
            vedtakshistorikken.setUngUfoer(fjernAapUngUfoerMedUgyldigeDatoer(vedtakshistorikken.getUngUfoer()));
            oppdaterAapSykepengeerstatningDatoer(vedtakshistorikken.getAap());

            LocalDate tidligsteDato = datoUtils.finnTidligsteDato(vedtakshistorikken);
            LocalDate tidligsteDatoBarnetillegg = datoUtils.finnTidligeDatoBarnetillegg(vedtakshistorikken.getBarnetillegg());

            if (tidligsteDato == null) {
                return;
            }

            var minimumAlder = Math.toIntExact(ChronoUnit.YEARS.between(tidligsteDato.minusYears(MIN_ALDER_AAP), LocalDate.now()));
            var maksimumAlder = getMaksimumAlder(vedtakshistorikken, minimumAlder);

            if (minimumAlder > maksimumAlder) {
                log.error("Kunne ikke opprette vedtakshistorikk på ident med minimum alder {}.", minimumAlder);
            } else {
                opprettVedtaksHistorikkResponse(avspillergruppeId, miljoe, responses, vedtakshistorikken, tidligsteDatoBarnetillegg, minimumAlder, maksimumAlder);
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

    private void opprettVedtaksHistorikkResponse(Long avspillergruppeId, String miljoe, Map<String, List<NyttVedtakResponse>> responses, Vedtakshistorikk vedtakshistorikken, LocalDate tidligsteDatoBarnetillegg,
                                                 int minimumAlder, int maksimumAlder) {
        List<String> identerIAldersgruppe = Collections.emptyList();
        try {
            if (tidligsteDatoBarnetillegg != null) {
                identerIAldersgruppe = identerUtils.getUtvalgteIdenterIAldersgruppeMedBarnUnder18(avspillergruppeId, 1, minimumAlder, maksimumAlder, miljoe, tidligsteDatoBarnetillegg);
            } else {
                identerIAldersgruppe = identerUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, 1, minimumAlder, maksimumAlder, miljoe);
            }
        } catch (RuntimeException e) {
            log.error("Kunne ikke hente utvalgte identer.", e);
        }

        if (!identerIAldersgruppe.isEmpty()) {
            responses.putAll(opprettHistorikkOgSendTilArena(avspillergruppeId, identerIAldersgruppe.get(0), miljoe, vedtakshistorikken));
        }
    }


    private Map<String, List<NyttVedtakResponse>> opprettHistorikkOgSendTilArena(
            Long avspillergruppeId,
            String personident,
            String miljoe,
            Vedtakshistorikk vedtakshistorikk
    ) {
        List<KontoinfoResponse> identerMedKontonummer = new ArrayList<>();
        if (vedtakshistorikk.getTvungenForvaltning() != null && !vedtakshistorikk.getTvungenForvaltning().isEmpty()) {
            var antallTvungenForvaltning = vedtakshistorikk.getTvungenForvaltning().size();
            identerMedKontonummer = identerUtils.getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallTvungenForvaltning);
        }

        List<RettighetRequest> rettigheter = new ArrayList<>();

        var ikkeAvluttendeAap115 = getIkkeAvsluttendeVedtakAap115(vedtakshistorikk.getAap115());
        var avsluttendeAap115 = getAvsluttendeVedtakAap115(vedtakshistorikk.getAap115());

        if (!opprettetNoedvendigInfoIPopp(vedtakshistorikk, personident, miljoe)) {
            return Collections.emptyMap();
        }

        opprettVedtakAap115(ikkeAvluttendeAap115, personident, miljoe, rettigheter);
        opprettVedtakAap(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakAap115(avsluttendeAap115, personident, miljoe, rettigheter);
        opprettVedtakUngUfoer(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakTvungenForvaltning(vedtakshistorikk, personident, miljoe, rettigheter, identerMedKontonummer);
        opprettVedtakFritakMeldekort(vedtakshistorikk, personident, miljoe, rettigheter);
        oppdaterTiltaksdeltakelse(vedtakshistorikk, personident, miljoe);
        opprettVedtakTiltaksdeltakelse(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettFoersteVedtakEndreDeltakerstatus(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakTiltakspenger(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakBarnetillegg(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettAvsluttendeVedtakEndreDeltakerstatus(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk, personident, miljoe, rettigheter);

        var senesteVedtak = datoUtils.finnSenesteVedtak(vedtakshistorikk.getAlleVedtak());

        List<RettighetRequest> rettighetRequests;
        if (senesteVedtak == null) {
            log.info("Kunne ikke opprette rettigheter for ident: " + personident);
            rettighetRequests = new ArrayList<>();
        } else if (senesteVedtak instanceof NyttVedtakAap) {
            rettighetRequests = arbeidsoekerUtils.opprettArbeidssoekerAap(personident, rettigheter, miljoe, ((NyttVedtakAap) senesteVedtak).getAktivitetsfase());
        } else if (senesteVedtak instanceof NyttVedtakTiltak) {
            rettighetRequests = arbeidsoekerUtils.opprettArbeidssoekerTiltak(rettigheter, miljoe);
        } else if (senesteVedtak instanceof NyttVedtakTillegg) {
            rettighetRequests = arbeidsoekerUtils.opprettArbeidssoekerTillegg(rettigheter, miljoe);
        } else {
            throw new VedtakshistorikkException("Ukjent vedtakstype: " + senesteVedtak.getClass());
        }

        return rettighetArenaForvalterConsumer.opprettRettighet(rettighetRequests);
    }

    private void oppdaterAapSykepengeerstatningDatoer(List<NyttVedtakAap> aapVedtak) {
        if (aapVedtak != null) {
            int antallDagerEndret = 0;
            for (var vedtak : aapVedtak) {
                if (AKTIVITETSFASE_SYKEPENGEERSTATNING.equals(vedtak.getAktivitetsfase()) && vedtak.getFraDato() != null) {
                    vedtak.setFraDato(vedtak.getFraDato().minusDays(antallDagerEndret));
                    if (vedtak.getTilDato() == null) {
                        vedtak.setTilDato(vedtak.getFraDato().plusMonths(6));
                    } else {
                        vedtak.setTilDato(vedtak.getTilDato().minusDays(antallDagerEndret));

                        var originalTilDato = vedtak.getTilDato();
                        vedtakUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder(vedtak, SYKEPENGEERSTATNING_MAKS_PERIODE);
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
            var rettighetRequest = new RettighetAap115Request(aap115);
            rettighetRequest.setPersonident(personident);
            rettighetRequest.setMiljoe(miljoe);
            rettighetRequest.getNyeAap115().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
            rettigheter.add(rettighetRequest);
        }
    }

    private boolean opprettetNoedvendigInfoIPopp(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe
    ) {
        var aap = historikk.getAap();
        if (aap != null && !aap.isEmpty()) {
            return rettighetAapService.opprettetPersonOgInntektIPopp(personident, miljoe, aap.get(0));
        }
        return true;
    }

    private void opprettVedtakAap(
            Vedtakshistorikk vedtak,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var aap = vedtak.getAap();
        if (aap != null && !aap.isEmpty()) {
            var rettighetRequest = new RettighetAapRequest(aap);
            rettighetRequest.setPersonident(personident);
            rettighetRequest.setMiljoe(miljoe);
            rettighetRequest.getNyeAap().forEach(rettighet ->
                    rettighet.setBegrunnelse(BEGRUNNELSE)
            );
            rettigheter.add(rettighetRequest);
        }
    }

    private void opprettVedtakUngUfoer(
            Vedtakshistorikk vedtak,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var foedselsdato = IdentUtil.getFoedselsdatoFraIdent(personident);
        var ungUfoer = vedtak.getUngUfoer();
        if (ungUfoer != null && !ungUfoer.isEmpty()) {
            var rettighetRequest = new RettighetUngUfoerRequest(ungUfoer);
            rettighetRequest.setPersonident(personident);
            rettighetRequest.setMiljoe(miljoe);

            var iterator = rettighetRequest.getNyeAaungufor().iterator();
            while (iterator.hasNext()) {
                var rettighet = iterator.next();
                rettighet.setBegrunnelse(BEGRUNNELSE);
                var alderPaaVedtaksdato = Math.toIntExact(ChronoUnit.YEARS.between(foedselsdato, rettighet.getFraDato()));
                if (alderPaaVedtaksdato < MIN_ALDER_UNG_UFOER || alderPaaVedtaksdato > MAX_ALDER_UNG_UFOER) {
                    log.error("Kan ikke opprette vedtak ung-ufør på ident som er {} år gammel.", alderPaaVedtaksdato);
                    iterator.remove();
                }
            }
            rettigheter.add(rettighetRequest);
        }
    }

    private void opprettVedtakTvungenForvaltning(
            Vedtakshistorikk vedtak,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter,
            List<KontoinfoResponse> identerMedKontonummer
    ) {
        var tvungenForvaltning = vedtak.getTvungenForvaltning();
        if (tvungenForvaltning != null && !tvungenForvaltning.isEmpty()) {
            var rettighetRequest = new RettighetTvungenForvaltningRequest(tvungenForvaltning);
            rettighetRequest.setPersonident(personident);
            rettighetRequest.setMiljoe(miljoe);
            for (var rettighet : rettighetRequest.getNyeAatfor()) {
                rettighet.setForvalter(ServiceUtils.buildForvalter(identerMedKontonummer.remove(identerMedKontonummer.size() - 1)));
                rettighet.setBegrunnelse(BEGRUNNELSE);
            }
            rettigheter.add(rettighetRequest);
        }
    }

    private void opprettVedtakFritakMeldekort(
            Vedtakshistorikk vedtak,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var fritakMeldekort = vedtak.getFritakMeldekort();
        if (fritakMeldekort != null && !fritakMeldekort.isEmpty()) {
            var rettighetRequest = new RettighetFritakMeldekortRequest(fritakMeldekort);
            rettighetRequest.setPersonident(personident);
            rettighetRequest.setMiljoe(miljoe);
            rettighetRequest.getNyeFritak().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
            rettigheter.add(rettighetRequest);
        }
    }

    private void oppdaterTiltaksdeltakelse(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe
    ) {
        var tiltaksdeltakelser = historikk.getTiltaksdeltakelse();
        if (tiltaksdeltakelser != null && !tiltaksdeltakelser.isEmpty()) {
            arbeidsoekerUtils.opprettArbeidssoekerTiltakdeltakelse(personident, miljoe);

            tiltaksdeltakelser.forEach(deltakelse -> {
                deltakelse.setFodselsnr(personident);
                deltakelse.setTiltakYtelse("J");
            });
            tiltaksdeltakelser.forEach(deltakelse -> {
                var tiltak = vedtakUtils.finnTiltak(personident, miljoe, deltakelse);

                if (tiltak != null) {
                    deltakelse.setTiltakId(tiltak.getTiltakId());
                    deltakelse.setTiltakProsentDeltid(tiltak.getTiltakProsentDeltid());
                    deltakelse.setFraDato(tiltak.getFraDato());
                    deltakelse.setTilDato(tiltak.getTilDato());
                }
            });

            var nyeTiltaksdeltakelser = tiltaksdeltakelser.stream()
                    .filter(deltakelse -> deltakelse.getTiltakId() != null).collect(Collectors.toList());

            nyeTiltaksdeltakelser = vedtakUtils.removeOverlappingTiltakVedtak(nyeTiltaksdeltakelser, historikk.getAap());

            historikk.setTiltaksdeltakelse(nyeTiltaksdeltakelser);
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
            List<NyttVedtakTiltak> nyeVedtakRequests = new ArrayList<>();

            for (var deltakelse : tiltaksdeltakelser) {
                nyeVedtakRequests.add(vedtakUtils.getVedtakForTiltaksdeltakelseRequest(deltakelse));
            }

            var rettighetRequest = new RettighetTiltaksdeltakelseRequest(nyeVedtakRequests);

            rettighetRequest.setPersonident(personident);
            rettighetRequest.setMiljoe(miljoe);
            rettigheter.add(rettighetRequest);

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
            if (vedtakUtils.canSetDeltakelseTilGjennomfoeres(deltakelse)) {
                List<String> endringer = vedtakUtils.getFoersteEndringerDeltakerstatus(deltakelse.getTiltakAdminKode());

                for (var endring : endringer) {
                    var rettighetRequest = vedtakUtils.opprettRettighetEndreDeltakerstatusRequest(personident, miljoe,
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
            List<RettighetRequest> rettigheter
    ) {
        var tiltaksdeltakelser = vedtak.getTiltaksdeltakelse();
        if (tiltaksdeltakelser != null && !tiltaksdeltakelser.isEmpty()) {
            for (var deltakelse : tiltaksdeltakelser) {
                if (vedtakUtils.canSetDeltakelseTilFinished(deltakelse)) {
                    var deltakerstatuskode = vedtakUtils.getAvsluttendeDeltakerstatus(deltakelse
                            .getTiltakAdminKode()).toString();

                    var rettighetRequest = vedtakUtils.opprettRettighetEndreDeltakerstatusRequest(personident, miljoe,
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

        List<NyttVedtakTiltak> nyeTiltakspenger = vedtakUtils.oppdaterVedtakslisteBasertPaaTiltaksdeltakelse(
                tiltakspenger, tiltaksdeltakelser);

        nyeTiltakspenger = vedtakUtils.removeOverlappingTiltakSequences(nyeTiltakspenger);

        if (nyeTiltakspenger != null && !nyeTiltakspenger.isEmpty()) {
            var rettighetRequest = new RettighetTiltakspengerRequest(nyeTiltakspenger);
            rettighetRequest.setPersonident(personident);
            rettighetRequest.setMiljoe(miljoe);
            rettighetRequest.getNyeTiltakspenger().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
            rettigheter.add(rettighetRequest);
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
            nyeBarnetillegg = vedtakUtils.oppdaterVedtakslisteBasertPaaTiltaksdeltakelse(
                    barnetillegg, tiltaksdeltakelser);

            nyeBarnetillegg = vedtakUtils.removeOverlappingTiltakSequences(nyeBarnetillegg);

            if (nyeBarnetillegg != null && !nyeBarnetillegg.isEmpty()) {
                var rettighetRequest = new RettighetTilleggsytelseRequest(nyeBarnetillegg);
                rettighetRequest.setPersonident(personident);
                rettighetRequest.setMiljoe(miljoe);
                rettighetRequest.getNyeTilleggsytelser().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
                rettigheter.add(rettighetRequest);
            }
        }

        historikk.setBarnetillegg(nyeBarnetillegg);
    }

    private void opprettVedtakTillegg(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var vedtak = oppdaterVedtakTillegg(historikk);

        if (vedtak != null && !vedtak.isEmpty() && !rettigheter.isEmpty()) {
            var rettighetRequest = new RettighetTilleggRequest(vedtak);
            rettighetRequest.setPersonident(personident);
            rettighetRequest.setMiljoe(miljoe);
            rettighetRequest.getNyeTilleggsstonad().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));

            opprettTiltaksaktivitet(rettigheter, rettighetRequest);

            rettigheter.add(rettighetRequest);
        }
    }


    private List<NyttVedtakTillegg> oppdaterVedtakTillegg(Vedtakshistorikk historikk) {
        var tillegg = historikk.getAlleTilleggVedtak();

        if (historikk.getAap() == null || historikk.getAap().isEmpty()) {
            tillegg = filtrerBortTilleggMedUoensketMaalgruppekode(tillegg, MAALGRUPPEKODE_TILKNYTTET_AAP);
        } else {
            tillegg = filtrerBortTilleggUtenGyldigTilknyttetVedtak(tillegg, historikk.getAap(),
                    MAALGRUPPEKODE_TILKNYTTET_AAP);
        }

        if (historikk.getTiltakspenger() == null || historikk.getTiltakspenger().isEmpty()) {
            tillegg = filtrerBortTilleggMedUoensketMaalgruppekode(tillegg, MAALGRUPPEKODE_TILKNYTTET_TILTAKSPENGER);
        } else {
            tillegg = filtrerBortTilleggUtenGyldigTilknyttetVedtak(tillegg, historikk.getTiltakspenger(),
                    MAALGRUPPEKODE_TILKNYTTET_TILTAKSPENGER);
        }
        return tillegg;

    }

    private List<NyttVedtakTillegg> filtrerBortTilleggMedUoensketMaalgruppekode(
            List<NyttVedtakTillegg> vedtak,
            String maalgruppekode
    ) {
        var filterteVedtak = vedtak;
        if (vedtak != null && !vedtak.isEmpty()) {
            filterteVedtak = vedtak.stream().filter(tillegg -> !tillegg.getMaalgruppeKode()
                    .equals(maalgruppekode)).collect(Collectors.toList());
        }
        return filterteVedtak;
    }

    private List<NyttVedtakTillegg> filtrerBortTilleggUtenGyldigTilknyttetVedtak(
            List<NyttVedtakTillegg> vedtak,
            List<? extends NyttVedtak> tilknyttetVedtak,
            String maalgruppekode
    ) {
        var filterteVedtak = vedtak;
        if (vedtak != null && !vedtak.isEmpty()) {
            filterteVedtak = vedtak.stream().filter(tillegg -> !tillegg.getMaalgruppeKode()
                    .equals(maalgruppekode) || (tillegg.getMaalgruppeKode()
                    .equals(maalgruppekode) && harGyldigTilknyttetVedtak(tillegg, tilknyttetVedtak)))
                    .collect(Collectors.toList());
        }
        return filterteVedtak;
    }

    private boolean harGyldigTilknyttetVedtak(NyttVedtakTillegg vedtak, List<? extends NyttVedtak> vedtaksliste) {
        if (vedtaksliste == null || vedtaksliste.isEmpty()) {
            return false;
        }
        var fraDato = vedtak.getVedtaksperiode().getFom();

        if (fraDato == null) {
            return false;
        }

        for (var item : vedtaksliste) {
            var fraDatoItem = item.getFraDato();
            var tilDatoItem = item.getTilDato();

            if (datoUtils.datoErInnenforPeriode(fraDato, fraDatoItem, tilDatoItem)) {
                return true;
            }
        }
        return false;
    }

    private void opprettTiltaksaktivitet(List<RettighetRequest> rettigheter, RettighetTilleggRequest request) {
        if (request.getVedtakTillegg() != null && !request.getVedtakTillegg().isEmpty()) {
            rettigheter.add(rettighetTiltakService.opprettRettighetTiltaksaktivitetRequest(
                    request, true));
        }
    }


}
