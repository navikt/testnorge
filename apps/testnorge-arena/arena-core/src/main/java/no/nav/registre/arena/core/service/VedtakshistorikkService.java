package no.nav.registre.arena.core.service;

import static com.google.common.base.Strings.isNullOrEmpty;
import static no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils.getFoedselsdatoFraFnr;
import static no.nav.registre.arena.core.service.util.ServiceUtils.BEGRUNNELSE;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MAX_ALDER_AAP;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MAX_ALDER_UNG_UFOER;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MIN_ALDER_AAP;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MIN_ALDER_UNG_UFOER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltakspengerRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.arena.core.service.util.ServiceUtils;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.aap.gensaksopplysninger.GensakKoder;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

@Slf4j
@Service
@RequiredArgsConstructor
public class VedtakshistorikkService {

    private final AapSyntConsumer aapSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final ServiceUtils serviceUtils;
    private final RettighetAapService rettighetAapService;
    private final RettighetTilleggService rettighetTilleggService;

    private static final LocalDate AVVIKLET_DATO_TSOTILFAM = LocalDate.of(2020, 02, 29);

    public Map<String, List<NyttVedtakResponse>> genererVedtakshistorikk(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var vedtakshistorikk = aapSyntConsumer.syntetiserVedtakshistorikk(antallNyeIdenter);
        Map<String, List<NyttVedtakResponse>> responses = new HashMap<>();
        for (var vedtakshistorikken : vedtakshistorikk) {
            vedtakshistorikken.setTilsynFamiliemedlemmer(fjernTilsynFamiliemedlemmerVedtakMedUgyldigeDatoer(vedtakshistorikken.getTilsynFamiliemedlemmer()));

            var tidligsteDato = LocalDate.now();
            var aap = finnUtfyltAap(vedtakshistorikken);
            var aapType = finnUtfyltAapType(vedtakshistorikken);
            var tiltak = finnUtfyltTiltak(vedtakshistorikken);
            var tillegg = finnUtfyltTillegg(vedtakshistorikken);
            var barnetillegg = vedtakshistorikken.getBarnetillegg();
            LocalDate tidligsteDatoBarnetillegg = null;

            if (!aap.isEmpty()) {
                tidligsteDato = finnTidligsteDatoAap(aap);
            } else if (!aapType.isEmpty()) {
                tidligsteDato = finnTidligsteDatoAapType(aapType);
            } else if (!tiltak.isEmpty()) {
                tidligsteDato = finnTidligsteDatoTiltak(tiltak);
            } else if (!tillegg.isEmpty()) {
                tidligsteDato = finnTidligsteDatoTillegg(tillegg);
            } else {
                continue;
            }

            if (barnetillegg != null && !barnetillegg.isEmpty()) {
                tidligsteDatoBarnetillegg = finnTidligsteDatoTiltak(barnetillegg);
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
                List<String> identerIAldersgruppe = Collections.emptyList();

                try {
                  if (tidligsteDatoBarnetillegg != null) {
                    identerIAldersgruppe = serviceUtils.getUtvalgteIdenterIAldersgruppeMedBarnUnder18(avspillergruppeId, 1, minimumAlder, maksimumAlder, miljoe, tidligsteDatoBarnetillegg);
                  } else {
                    identerIAldersgruppe = serviceUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, 1, minimumAlder, maksimumAlder, miljoe);
                  }
                } catch (RuntimeException e) {
                    log.error("Kunne ikke hente ident fra hodejegeren");
                }

                if (!identerIAldersgruppe.isEmpty()) {
                    responses.putAll(opprettHistorikkOgSendTilArena(avspillergruppeId, identerIAldersgruppe.get(0), miljoe, vedtakshistorikken));
                }
            }
        }
        return responses;
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
            identerMedKontonummer = serviceUtils.getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallTvungenForvaltning);
        }

        List<RettighetRequest> rettigheter = new ArrayList<>();

        opprettVedtakAap115(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakAap(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakUngUfoer(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakTvungenForvaltning(vedtakshistorikk, personident, miljoe, rettigheter, identerMedKontonummer);
        opprettVedtakFritakMeldekort(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakTiltakspenger(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakBarnetillegg(vedtakshistorikk, personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getBoutgifter(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getDagligReise(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getFlytting(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getLaeremidler(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getHjemreise(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getReiseObligatoriskSamling(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getTilsynBarn(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getTilsynFamiliemedlemmer(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getBoutgifterArbeidssoekere(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getDagligReiseArbeidssoekere(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getFlyttingArbeidssoekere(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getLaeremidlerArbeidssoekere(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getHjemreiseArbeidssoekere(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getReisestoenadArbeidssoekere(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getReiseObligatoriskSamlingArbeidssoekere(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getTilsynBarnArbeidssoekere(), personident, miljoe, rettigheter);
        opprettVedtakTillegg(vedtakshistorikk.getTilsynFamiliemedlemmerArbeidssoekere(), personident, miljoe, rettigheter);

        return rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));
    }

    private List<NyttVedtakTillegg> fjernTilsynFamiliemedlemmerVedtakMedUgyldigeDatoer(List<NyttVedtakTillegg> tilsynFamiliemedlemmer) {
        List<NyttVedtakTillegg> nyTilsynFamiliemedlemmer = new ArrayList<>();
        if (tilsynFamiliemedlemmer != null){
            nyTilsynFamiliemedlemmer = tilsynFamiliemedlemmer.stream().filter(vedtak ->
                    !vedtak.getFraDato().isAfter(AVVIKLET_DATO_TSOTILFAM))
                    .collect(Collectors.toList());
        }

        return nyTilsynFamiliemedlemmer.isEmpty() ? null : nyTilsynFamiliemedlemmer;
    }

    private LocalDate finnTidligsteDatoAap(List<NyttVedtakAap> vedtak) {
        var tidligsteDato = LocalDate.now();
        for (var vedtaket : vedtak) {
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getFraDato());
            var genSaksopplysninger = vedtaket.getGenSaksopplysninger();
            for (var saksopplysning : genSaksopplysninger) {
                if (GensakKoder.KDATO.equals(saksopplysning.getKode())
                        || GensakKoder.BTID.equals(saksopplysning.getKode())
                        || GensakKoder.UFTID.equals(saksopplysning.getKode())
                        || GensakKoder.YDATO.equals(saksopplysning.getKode())) {
                    if (!isNullOrEmpty(saksopplysning.getVerdi())) {
                        tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, LocalDate.parse(saksopplysning.getVerdi(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    }
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

    private void opprettVedtakAap115(
            Vedtakshistorikk vedtak,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var aap115 = vedtak.getAap115();
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
            rettighetRequest.getNyeAap().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
            rettigheter.add(rettighetRequest);
        }
    }

    private void opprettVedtakUngUfoer(
            Vedtakshistorikk vedtak,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var foedselsdato = getFoedselsdatoFraFnr(personident);
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

    private void opprettVedtakTiltakspenger(
            Vedtakshistorikk vedtak,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var tiltakspenger = vedtak.getTiltakspenger();
        if (tiltakspenger != null && !tiltakspenger.isEmpty()) {
            var rettighetRequest = new RettighetTiltakspengerRequest(tiltakspenger);
            rettighetRequest.setPersonident(personident);
            rettighetRequest.setMiljoe(miljoe);
            rettighetRequest.getNyeTiltakspenger().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
            rettigheter.add(rettighetRequest);
        }
    }

    private void opprettVedtakBarnetillegg(
            Vedtakshistorikk vedtak,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var barnetillegg = vedtak.getBarnetillegg();
        if (barnetillegg != null && !barnetillegg.isEmpty()) {
            var rettighetRequest = new RettighetTilleggsytelseRequest(barnetillegg);
            rettighetRequest.setPersonident(personident);
            rettighetRequest.setMiljoe(miljoe);
            rettighetRequest.getNyeTilleggsytelser().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
            rettigheter.add(rettighetRequest);
        }
    }

    private void opprettVedtakTillegg(
            List<NyttVedtakTillegg> vedtak,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        if (vedtak != null && !vedtak.isEmpty()) {
            rettighetTilleggService.opprettTiltaksaktiviteter(rettigheter);
            var rettighetRequest = new RettighetTilleggRequest(vedtak);
            rettighetRequest.setPersonident(personident);
            rettighetRequest.setMiljoe(miljoe);
            rettighetRequest.getNyeTilleggsstonad().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
            rettigheter.add(rettighetRequest);
        }
    }

    private List<NyttVedtakAap> finnUtfyltAap(Vedtakshistorikk vedtakshistorikk) {
        var aap = vedtakshistorikk.getAap();

        if (aap != null && !aap.isEmpty()) {
            return aap;
        }

        return Collections.emptyList();
    }

    private List<NyttVedtakAap> finnUtfyltAapType(Vedtakshistorikk vedtakshistorikk) {
        var aap115 = vedtakshistorikk.getAap115();
        var ungUfoer = vedtakshistorikk.getUngUfoer();
        var tvungenForvaltning = vedtakshistorikk.getTvungenForvaltning();
        var fritakMeldekort = vedtakshistorikk.getFritakMeldekort();

        if (aap115 != null && !aap115.isEmpty()) {
            return aap115;
        }
        if (ungUfoer != null && !ungUfoer.isEmpty()) {
            return ungUfoer;
        }
        if (tvungenForvaltning != null && !tvungenForvaltning.isEmpty()) {
            return tvungenForvaltning;
        }
        if (fritakMeldekort != null && !fritakMeldekort.isEmpty()) {
            return fritakMeldekort;
        }

        return Collections.emptyList();
    }

    private List<NyttVedtakTiltak> finnUtfyltTiltak(Vedtakshistorikk vedtakshistorikk) {
        var tiltakspenger = vedtakshistorikk.getTiltakspenger();
        var barnetillegg = vedtakshistorikk.getBarnetillegg();

        if (tiltakspenger != null && !tiltakspenger.isEmpty()) {
            return tiltakspenger;
        }

        if (barnetillegg != null && !barnetillegg.isEmpty()) {
            return barnetillegg;
        }

        return Collections.emptyList();
    }

    private List<NyttVedtakTillegg> finnUtfyltTillegg(Vedtakshistorikk vedtakshistorikk) {
        var boutgifter = vedtakshistorikk.getBoutgifter();
        var dagligReise = vedtakshistorikk.getDagligReise();
        var flytting = vedtakshistorikk.getFlytting();
        var laeremidler = vedtakshistorikk.getLaeremidler();
        var hjemreise = vedtakshistorikk.getHjemreise();
        var reiseObligatoriskSamling = vedtakshistorikk.getReiseObligatoriskSamling();
        var tilsynBarn = vedtakshistorikk.getTilsynBarn();
        var tilsynFamiliemedlemmer = vedtakshistorikk.getTilsynFamiliemedlemmer();
        var boutgifterArbeidssoekere = vedtakshistorikk.getBoutgifterArbeidssoekere();
        var dagligReiseArbeidssoekere = vedtakshistorikk.getDagligReiseArbeidssoekere();
        var flyttingArbeidssoekere = vedtakshistorikk.getFlyttingArbeidssoekere();
        var laeremidlerArbeidssoekere = vedtakshistorikk.getLaeremidlerArbeidssoekere();
        var hjemreiseArbeidssoekere = vedtakshistorikk.getHjemreiseArbeidssoekere();
        var reisestoenadArbeidssoekere = vedtakshistorikk.getReisestoenadArbeidssoekere();
        var reiseObligatoriskSamlingArbeidssoekere = vedtakshistorikk.getReiseObligatoriskSamlingArbeidssoekere();
        var tilsynBarnArbeidssoekere = vedtakshistorikk.getTilsynBarnArbeidssoekere();
        var tilsynFamiliemedlemmerArbeidssoekere = vedtakshistorikk.getTilsynFamiliemedlemmerArbeidssoekere();

        if (boutgifter != null && !boutgifter.isEmpty()) {
            return boutgifter;
        }
        if (dagligReise != null && !dagligReise.isEmpty()) {
            return dagligReise;
        }
        if (flytting != null && !flytting.isEmpty()) {
            return flytting;
        }
        if (laeremidler != null && !laeremidler.isEmpty()) {
            return laeremidler;
        }
        if (hjemreise != null && !hjemreise.isEmpty()) {
            return hjemreise;
        }
        if (reiseObligatoriskSamling != null && !reiseObligatoriskSamling.isEmpty()) {
            return reiseObligatoriskSamling;
        }
        if (tilsynBarn != null && !tilsynBarn.isEmpty()) {
            return tilsynBarn;
        }
        if (tilsynFamiliemedlemmer != null && !tilsynFamiliemedlemmer.isEmpty()) {
            return tilsynFamiliemedlemmer;
        }

        if (boutgifterArbeidssoekere != null && !boutgifterArbeidssoekere.isEmpty()) {
            return boutgifterArbeidssoekere;
        }
        if (dagligReiseArbeidssoekere != null && !dagligReiseArbeidssoekere.isEmpty()) {
            return dagligReiseArbeidssoekere;
        }
        if (flyttingArbeidssoekere != null && !flyttingArbeidssoekere.isEmpty()) {
            return flyttingArbeidssoekere;
        }
        if (laeremidlerArbeidssoekere != null && !laeremidlerArbeidssoekere.isEmpty()) {
            return laeremidlerArbeidssoekere;
        }
        if (hjemreiseArbeidssoekere != null && !hjemreiseArbeidssoekere.isEmpty()) {
            return hjemreiseArbeidssoekere;
        }
        if (reisestoenadArbeidssoekere != null && !reisestoenadArbeidssoekere.isEmpty()) {
            return reisestoenadArbeidssoekere;
        }
        if (reiseObligatoriskSamlingArbeidssoekere != null && !reiseObligatoriskSamlingArbeidssoekere.isEmpty()) {
            return reiseObligatoriskSamlingArbeidssoekere;
        }
        if (tilsynBarnArbeidssoekere != null && !tilsynBarnArbeidssoekere.isEmpty()) {
            return tilsynBarnArbeidssoekere;
        }
        if (tilsynFamiliemedlemmerArbeidssoekere != null && !tilsynFamiliemedlemmerArbeidssoekere.isEmpty()) {
            return tilsynFamiliemedlemmerArbeidssoekere;
        }

        return Collections.emptyList();
    }
}
