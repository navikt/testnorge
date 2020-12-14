package no.nav.registre.arena.core.service;

import static com.google.common.base.Strings.isNullOrEmpty;
import static no.nav.registre.arena.core.consumer.rs.AapSyntConsumer.ARENA_AAP_UNG_UFOER_DATE_LIMIT;
import static no.nav.registre.arena.core.consumer.rs.TilleggSyntConsumer.ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT;
import static no.nav.registre.arena.core.service.RettighetAapService.SYKEPENGEERSTATNING_MAKS_PERIODE;
import static no.nav.registre.arena.core.service.util.ServiceUtils.AKTIVITETSFASE_SYKEPENGEERSTATNING;
import static no.nav.registre.arena.core.service.util.ServiceUtils.BEGRUNNELSE;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MAX_ALDER_AAP;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MAX_ALDER_UNG_UFOER;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MIN_ALDER_AAP;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MIN_ALDER_UNG_UFOER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.arena.core.service.util.ServiceUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.arena.core.consumer.rs.AapSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetAap115Request;
import no.nav.registre.arena.core.consumer.rs.request.RettighetAapRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFritakMeldekortRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTilleggRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTilleggsytelseRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltaksdeltakelseRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltakspengerRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.arena.core.service.exception.VedtakshistorikkException;
import no.nav.registre.arena.core.service.util.ArbeidssoekerUtils;
import no.nav.registre.arena.core.service.util.IdenterUtils;
import no.nav.registre.arena.core.service.util.VedtakUtils;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.aap.gensaksopplysninger.GensakKoder;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Deltakerstatuser;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import no.nav.registre.testnorge.libs.core.util.IdentUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class VedtakshistorikkService {

    private final AapSyntConsumer aapSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final IdenterUtils identerUtils;
    private final ArbeidssoekerUtils arbeidsoekerUtils;
    private final VedtakUtils vedtakUtils;
    private final RettighetAapService rettighetAapService;
    private final RettighetTiltakService rettighetTiltakService;

    public Map<String, List<NyttVedtakResponse>> genererVedtakshistorikk(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var vedtakshistorikk = aapSyntConsumer.syntetiserVedtakshistorikk(antallNyeIdenter);
        Map<String, List<NyttVedtakResponse>> responses = new HashMap<>();
        for (var vedtakshistorikken : vedtakshistorikk) {
            vedtakshistorikken.setTilsynFamiliemedlemmer(fjernTilsynFamiliemedlemmerVedtakMedUgyldigeDatoer(vedtakshistorikken.getTilsynFamiliemedlemmer()));
            vedtakshistorikken.setUngUfoer(fjernAapUngUfoerMedUgyldigeDatoer(vedtakshistorikken.getUngUfoer()));
            oppdaterAapSykepengeerstatningDatoer(vedtakshistorikken.getAap());

            LocalDate tidligsteDato = settTidligsteDato(vedtakshistorikken);
            LocalDate tidligsteDatoBarnetillegg = settTidligeDatoBarnetillegg(vedtakshistorikken.getBarnetillegg());

            if (tidligsteDato == null) {
                continue;
            }

            var minimumAlder = Math.toIntExact(ChronoUnit.YEARS.between(tidligsteDato.minusYears(MIN_ALDER_AAP), LocalDate.now()));
            if (minimumAlder > MAX_ALDER_AAP) {
                log.error("Kunne ikke opprette vedtakshistorikk på ident med minimum alder {}", minimumAlder);
                continue;
            }
            var maksimumAlder = minimumAlder + 50;
            if (maksimumAlder > MAX_ALDER_AAP) {
                maksimumAlder = MAX_ALDER_AAP;
            }

            var ungUfoer = vedtakshistorikken.getUngUfoer();
            if (ungUfoer != null && !ungUfoer.isEmpty()) {
                maksimumAlder = MAX_ALDER_UNG_UFOER;
            }

            if (minimumAlder > maksimumAlder) {
                log.error("Kunne ikke finne ident i riktig aldersgruppe");
            } else {
                opprettVedtaksHistorikkResponse(avspillergruppeId, miljoe, responses, vedtakshistorikken, tidligsteDatoBarnetillegg, minimumAlder, maksimumAlder);
            }
        }
        return responses;
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

    private LocalDate settTidligsteDato(Vedtakshistorikk vedtakshistorikken) {

        LocalDate tidligsteDato;
        var aap = finnUtfyltAap(vedtakshistorikken);
        var aapType = vedtakshistorikken.getAlleAapVedtak();
        var tiltak = vedtakshistorikken.getAlleTiltakVedtak();
        var tillegg = vedtakshistorikken.getAlleTilleggVedtak();

        if (!aap.isEmpty()) {
            tidligsteDato = finnTidligsteDatoAap(aap);
        } else if (!aapType.isEmpty()) {
            tidligsteDato = finnTidligsteDatoAapType(aapType);
        } else if (!tiltak.isEmpty()) {
            tidligsteDato = finnTidligsteDatoTiltak(tiltak);
        } else if (!tillegg.isEmpty()) {
            tidligsteDato = finnTidligsteDatoTillegg(tillegg);
        } else {
            return null;
        }
        return tidligsteDato;
    }

    private LocalDate settTidligeDatoBarnetillegg(List<NyttVedtakTiltak> barnetillegg) {
        if (barnetillegg != null && !barnetillegg.isEmpty()) {
            return finnTidligsteDatoTiltak(barnetillegg);
        }
        return null;
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

        opprettVedtakAap115(ikkeAvluttendeAap115, personident, miljoe, rettigheter);
        opprettVedtakAap(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakAap115(avsluttendeAap115, personident, miljoe, rettigheter);
        opprettVedtakUngUfoer(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakTvungenForvaltning(vedtakshistorikk, personident, miljoe, rettigheter, identerMedKontonummer);
        opprettVedtakFritakMeldekort(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakTiltaksdeltakelse(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettFoersteVedtakEndreDeltakerstatus(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakTiltakspenger(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakBarnetillegg(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettAvsluttendeVedtakEndreDeltakerstatus(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getAlleTilleggVedtak(), personident, miljoe, rettigheter);

        var senesteVedtak = finnSenesteVedtak(vedtakshistorikk.getAlleVedtak());

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

    private LocalDate finnTidligsteDatoAap(List<NyttVedtakAap> vedtak) {
        var tidligsteDato = LocalDate.now();
        for (var vedtaket : vedtak) {
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getFraDato());
            var genSaksopplysninger = vedtaket.getGenSaksopplysninger();
            for (var saksopplysning : genSaksopplysninger) {
                if (isNullOrEmpty(saksopplysning.getVerdi())) {
                    continue;
                }
                if (GensakKoder.KDATO.equals(saksopplysning.getKode())
                        || GensakKoder.BTID.equals(saksopplysning.getKode())
                        || GensakKoder.UFTID.equals(saksopplysning.getKode())
                        || GensakKoder.YDATO.equals(saksopplysning.getKode())) {
                    tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, LocalDate.parse(saksopplysning.getVerdi(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                }
            }
        }
        return tidligsteDato;
    }

    private LocalDate finnTidligsteDatoAapType(List<NyttVedtakAap> vedtak) {
        var tidligsteDato = LocalDate.now();
        for (var vedtaket : vedtak) {
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getFraDato());
        }
        return tidligsteDato;
    }

    private LocalDate finnTidligsteDatoTiltak(List<NyttVedtakTiltak> vedtak) {
        var tidligsteDato = LocalDate.now();
        for (var vedtaket : vedtak) {
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getFraDato());
        }
        return tidligsteDato;
    }

    private LocalDate finnTidligsteDatoTillegg(List<NyttVedtakTillegg> vedtak) {
        var tidligsteDato = LocalDate.now();
        for (var vedtaket : vedtak) {
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getFraDato());
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getVedtaksperiode().getFom());
        }
        return tidligsteDato;
    }

    private LocalDate finnTidligsteDatoAvTo(
            LocalDate date1,
            LocalDate date2
    ) {
        if (date2 == null) {
            return date1;
        }
        if (date2.isBefore(date1)) {
            return date2;
        } else {
            return date1;
        }
    }

    private NyttVedtak finnSenesteVedtak(List<? extends NyttVedtak> vedtak) {
        var senesteDato = LocalDate.MIN;
        NyttVedtak senesteVedtak = null;
        for (var vedtaket : vedtak) {
            LocalDate vedtakFraDato;
            if (vedtaket instanceof NyttVedtakTillegg) {
                vedtakFraDato = ((NyttVedtakTillegg) vedtaket).getVedtaksperiode().getFom();
            } else {
                vedtakFraDato = vedtaket.getFraDato();
            }
            if (vedtakFraDato != null && senesteDato.isBefore(vedtakFraDato)) {
                senesteDato = vedtakFraDato;
                senesteVedtak = vedtaket;
            }
        }
        return senesteVedtak;
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

    private void opprettVedtakAap(
            Vedtakshistorikk vedtak,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var aap = vedtak.getAap();
        if (aap != null && !aap.isEmpty()) {
            rettighetAapService.opprettPersonOgInntektIPopp(personident, miljoe, aap.get(0));
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

    private void opprettVedtakTiltaksdeltakelse(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
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
                    deltakelse.setFraDato(tiltak.getFraDato());
                    deltakelse.setTilDato(tiltak.getTilDato());
                }
            });

            var nyeTiltaksdeltakelser = tiltaksdeltakelser.stream()
                    .filter(deltakelse -> deltakelse.getTiltakId() != null).collect(Collectors.toList());

            nyeTiltaksdeltakelser = vedtakUtils.removeOverlappingTiltakVedtak(nyeTiltaksdeltakelser, historikk.getAap());

            if (nyeTiltaksdeltakelser != null && !nyeTiltaksdeltakelser.isEmpty()) {
                List<NyttVedtakTiltak> nyeVedtakRequests = new ArrayList<>();
                for (var deltakelse : nyeTiltaksdeltakelser) {
                    nyeVedtakRequests.add(vedtakUtils.getVedtakForTiltaksdeltakelseRequest(deltakelse));
                }

                var rettighetRequest = new RettighetTiltaksdeltakelseRequest(nyeVedtakRequests);

                rettighetRequest.setPersonident(personident);
                rettighetRequest.setMiljoe(miljoe);
                rettigheter.add(rettighetRequest);
            }
            historikk.setTiltaksdeltakelse(nyeTiltaksdeltakelser);
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
            List<NyttVedtakTillegg> vedtak,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        if (vedtak != null && !vedtak.isEmpty()) {
            var rettighetRequest = new RettighetTilleggRequest(vedtak);
            rettighetRequest.setPersonident(personident);
            rettighetRequest.setMiljoe(miljoe);
            rettighetRequest.getNyeTilleggsstonad().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));

            opprettTiltaksaktivitet(rettigheter, rettighetRequest);

            rettigheter.add(rettighetRequest);
        }
    }

    private void opprettTiltaksaktivitet(List<RettighetRequest> rettigheter, RettighetTilleggRequest request) {
        if (request.getVedtakTillegg() != null && !request.getVedtakTillegg().isEmpty()) {
            rettigheter.add(rettighetTiltakService.opprettRettighetTiltaksaktivitetRequest(
                    request, true));
        }
    }

    private List<NyttVedtakAap> finnUtfyltAap(Vedtakshistorikk vedtakshistorikk) {
        var aap = vedtakshistorikk.getAap();

        if (aap != null && !aap.isEmpty()) {
            return aap;
        }

        return Collections.emptyList();
    }

}
