package no.nav.registre.arena.core.service;

import static com.google.common.base.Strings.isNullOrEmpty;
import static no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils.getFoedselsdatoFraFnr;
import static no.nav.registre.arena.core.service.util.ServiceUtils.BEGRUNNELSE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.AapSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetAap115Request;
import no.nav.registre.arena.core.consumer.rs.request.RettighetAapRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFritakMeldekortRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.arena.core.service.util.ServiceUtils;
import no.nav.registre.arena.domain.aap.gensaksopplysninger.GensakKoder;
import no.nav.registre.arena.domain.historikk.Vedtakshistorikk;
import no.nav.registre.arena.domain.vedtak.NyttVedtakAap;
import no.nav.registre.arena.domain.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class RettighetAapService {

    private static final int MIN_ALDER_UNG_UFOER = 18;
    private static final int MAX_ALDER_UNG_UFOER = 36;
    private static final int MIN_ALDER_VEDTAKSHISTORIKK = 18;
    private static final int MAX_ALDER_VEDTAKSHISTORIKK = 67;

    private final AapSyntConsumer aapSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final BrukereService brukereService;
    private final ServiceUtils serviceUtils;

    public List<NyttVedtakResponse> genererVedtakshistorikk(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var vedtakshistorikk = aapSyntConsumer.syntetiserVedtakshistorikk(antallNyeIdenter);
        List<NyttVedtakResponse> responses = new ArrayList<>();
        for (var vedtakshistorikken : vedtakshistorikk) {
            var aap = vedtakshistorikken.getAap();

            if (aap != null && !aap.isEmpty()) {
                var tidligsteDato = finnTidligsteDato(aap);
                var minimumAlder = Math.toIntExact(ChronoUnit.YEARS.between(tidligsteDato.minusYears(MIN_ALDER_VEDTAKSHISTORIKK), LocalDate.now()));
                if (minimumAlder > MAX_ALDER_VEDTAKSHISTORIKK) {
                    log.warn("Kunne ikke opprette vedtakshistorikk på ident med minimum alder {}", minimumAlder);
                    continue;
                }
                var maksimumAlder = minimumAlder + 50;
                if (maksimumAlder > MAX_ALDER_VEDTAKSHISTORIKK) {
                    maksimumAlder = MAX_ALDER_VEDTAKSHISTORIKK;
                }
                var ident = serviceUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, 1, minimumAlder, maksimumAlder).get(0);
                responses.addAll(opprettHistorikkOgSendTilArena(avspillergruppeId, ident, miljoe, vedtakshistorikken));
            }
        }
        return responses;
    }

    public List<NyttVedtakResponse> genererAapMedTilhoerende115(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetAap(utvalgteIdenter.size());

        List<RettighetRequest> aap115Rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            var ident = utvalgteIdenter.remove(utvalgteIdenter.size() - 1);
            var aap115 = aapSyntConsumer.syntetiserRettighetAap115(syntetisertRettighet.getFraDato().minusDays(1), syntetisertRettighet.getTilDato()).get(0);
            aap115.setBegrunnelse(BEGRUNNELSE);
            var aap115Rettighet = new RettighetAap115Request(Collections.singletonList(aap115));
            aap115Rettighet.setPersonident(ident);
            aap115Rettighet.setMiljoe(miljoe);
            aap115Rettigheter.add(aap115Rettighet);

            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetAapRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(ident);
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(aap115Rettigheter, miljoe));
        return rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));
    }

    public List<NyttVedtakResponse> genererAap115(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetAap115(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetAap115Request(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));
    }

    public List<NyttVedtakResponse> genererUngUfoer(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter, MIN_ALDER_UNG_UFOER, MAX_ALDER_UNG_UFOER);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetUngUfoer(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetUngUfoerRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));
    }

    public List<NyttVedtakResponse> genererTvungenForvaltning(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var identerMedKontonummer = serviceUtils.getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallNyeIdenter);
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetTvungenForvaltning(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            syntetisertRettighet.setForvalter(ServiceUtils.buildForvalter(identerMedKontonummer.remove(identerMedKontonummer.size() - 1)));
            var rettighetRequest = new RettighetTvungenForvaltningRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));
    }

    public List<NyttVedtakResponse> genererFritakMeldekort(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = brukereService.hentEksisterendeArbeidsoekerIdenter();
        var identerIAvspillergruppe = new HashSet<>(serviceUtils.getLevende(avspillergruppeId));
        utvalgteIdenter.retainAll(identerIAvspillergruppe);
        Collections.shuffle(utvalgteIdenter);
        utvalgteIdenter = utvalgteIdenter.subList(0, antallNyeIdenter);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetFritakMeldekort(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetFritakMeldekortRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);
    }

    private List<NyttVedtakResponse> opprettHistorikkOgSendTilArena(
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

        return rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));
    }

    private LocalDate finnTidligsteDato(List<NyttVedtakAap> aapVedtak) {
        LocalDate tidligsteDato = LocalDate.now();
        for (var aapVedtaket : aapVedtak) {
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, aapVedtaket.getFraDato());
            var genSaksopplysninger = aapVedtaket.getGenSaksopplysninger();
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
                    log.warn("Kan ikke opprette vedtak ung-ufør på ident som er {} år gammel.", alderPaaVedtaksdato);
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
}
