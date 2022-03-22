package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.ArenaForvalterConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.EndreInnsatsbehovRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.RettighetRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.exception.VedtakshistorikkException;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ArenaBrukerUtils;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.NyBruker;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.NyEndreInnsatsbehov;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ArenaBrukerUtils.checkNyeBrukereResponse;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ArenaBrukerUtils.hentIdentListe;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArenaForvalterService {

    private static final String INGEN_OPPFOELGING = "N";
    private static final String MED_OPPFOELGING = "J";
    private static final String IARBS_HOVEDMAAL = "BEHOLDEA";

    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final Random random = new Random();
    private final ArenaBrukerUtils arenaBrukerUtils;

    public NyeBrukereResponse sendArbeidssoekereTilArenaForvalter(
            List<String> identer,
            String miljoe,
            Kvalifiseringsgrupper kvalifiseringsgruppe,
            String oppfolging,
            LocalDate aktiveringsDato
    ) {
        var nyeBrukere = identer.stream().map(ident ->
                NyBruker.builder()
                        .personident(ident)
                        .miljoe(miljoe)
                        .kvalifiseringsgruppe(kvalifiseringsgruppe)
                        .automatiskInnsendingAvMeldekort(true)
                        .oppfolging(oppfolging)
                        .aktiveringsDato(aktiveringsDato)
                        .build())
                .collect(Collectors.toList());

        return arenaForvalterConsumer.sendBrukereTilArenaForvalter(nyeBrukere);
    }


    public Map<String, NyeBrukereResponse> opprettArbeidssoekereUtenVedtak(
            List<String> identer,
            String miljoe
    ) {
        Map<String, NyeBrukereResponse> responses = new HashMap<>();
        for (var ident : identer) {
            var res = opprettArbeidssoekerUtenVedtak(ident, miljoe);
            responses.put(ident, res);
        }
        return responses;
    }

    private NyeBrukereResponse opprettArbeidssoekerUtenVedtak(
            String ident,
            String miljoe
    ) {
        var kvalifiseringsgruppe = getKvalifiseringsgruppeForOppfoelging();
        return sendArbeidssoekereTilArenaForvalter(Collections.singletonList(ident), miljoe, kvalifiseringsgruppe, MED_OPPFOELGING, null);
    }

    public boolean arbeidssoekerIkkeOpprettetIArena(String personident) {
        var identerIArena = hentIdentListe(arenaForvalterConsumer.hentArbeidsoekere(personident, null, null));
        return !identerIArena.contains(personident);
    }

    public void opprettArbeidssoekerVedtakshistorikk(
            String personident,
            String miljoe,
            NyttVedtak senesteVedtak,
            LocalDate aktiveringsDato
    ) {
        if (senesteVedtak.getRettighetType() == RettighetType.AAP) {
            opprettArbeidssoekerAap(personident, miljoe, ((NyttVedtakAap) senesteVedtak).getAktivitetsfase(), aktiveringsDato);
        } else if (senesteVedtak.getRettighetType() == RettighetType.TILTAK) {
            opprettArbeidssoeker(personident, miljoe, random.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.BFORM, aktiveringsDato);
        } else if (senesteVedtak.getRettighetType() == RettighetType.TILLEGG) {
            opprettArbeidssoeker(personident, miljoe, random.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.BFORM, aktiveringsDato);
        } else {
            throw new VedtakshistorikkException("Mangler støtte for rettighettype: " + senesteVedtak.getRettighetType());
        }
    }

    public Kvalifiseringsgrupper opprettArbeidssoekerTiltaksdeltakelse(
            String personident,
            String miljoe,
            RettighetType rettighetType,
            LocalDate aktiveringsDato
    ) {
        Kvalifiseringsgrupper kvalifiseringsgruppe;
        if (rettighetType == RettighetType.AAP) {
            kvalifiseringsgruppe = random.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.VARIG;
        } else if (rettighetType == RettighetType.TILTAK) {
            kvalifiseringsgruppe = random.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.BFORM;
        } else if (rettighetType == RettighetType.TILLEGG) {
            kvalifiseringsgruppe = random.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.BFORM;
        } else {
            throw new VedtakshistorikkException("Mangler støtte for rettighettype: " + rettighetType);
        }

        opprettArbeidssoeker(personident, miljoe, kvalifiseringsgruppe, aktiveringsDato);
        return kvalifiseringsgruppe;
    }

    private void opprettArbeidssoekerAap(
            String personident,
            String miljoe,
            String aktivitetsfase,
            LocalDate aktiveringsDato
    ) {
        if (isNull(aktivitetsfase) || aktivitetsfase.isBlank()) {
            opprettArbeidssoeker(personident, miljoe, Kvalifiseringsgrupper.BATT, aktiveringsDato);
        } else {
            var formidlingsgruppe = arenaBrukerUtils.velgFormidlingsgruppeBasertPaaAktivitetsfase(aktivitetsfase);
            var kvalifiseringsgruppe = arenaBrukerUtils.velgKvalifiseringsgruppeBasertPaaFormidlingsgruppe(aktivitetsfase, formidlingsgruppe);
            opprettArbeidssoeker(personident, miljoe, kvalifiseringsgruppe, aktiveringsDato);

            if (formidlingsgruppe.equals("IARBS")) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.warn("Thread interrupted");
                }
                endreFormidlingsgruppeForBrukerTilIarbs(personident, miljoe, kvalifiseringsgruppe);
            }
        }
    }

    private void opprettArbeidssoeker(
            String personident,
            String miljoe,
            Kvalifiseringsgrupper kvalifiseringsgruppe,
            LocalDate aktiveringsDato
    ) {
        if (arbeidssoekerIkkeOpprettetIArena(personident)) {
            var nyeBrukereResponse = sendArbeidssoekereTilArenaForvalter(Collections.singletonList(personident), miljoe, kvalifiseringsgruppe, INGEN_OPPFOELGING, aktiveringsDato);
            checkNyeBrukereResponse(nyeBrukereResponse, personident);
        }
    }

    private Kvalifiseringsgrupper getKvalifiseringsgruppeForOppfoelging() {
        var r = random.nextDouble();
        if (r > 0.5) {
            return Kvalifiseringsgrupper.IKVAL;
        }
        return r > 0.2 ? Kvalifiseringsgrupper.BFORM : Kvalifiseringsgrupper.BKART;
    }

    private void endreFormidlingsgruppeForBrukerTilIarbs(String personident, String miljoe, Kvalifiseringsgrupper kvalifiseringsgruppe) {
        log.info("Endrer formidlingsgruppe til IARBS for ident: " + personident);
        var request = EndreInnsatsbehovRequest.builder()
                .personident(personident)
                .miljoe(miljoe)
                .nyeEndreInnsatsbehov(Collections.singletonList(NyEndreInnsatsbehov.builder()
                        .kvalifiseringsgruppe(kvalifiseringsgruppe)
                        .hovedmaal(IARBS_HOVEDMAAL)
                        .build()))
                .build();
        arenaForvalterConsumer.endreInnsatsbehovForBruker(request);
    }

    public Map<String, List<NyttVedtakResponse>> opprettRettigheterIArena(
            List<RettighetRequest> rettigheter
    ){
        return arenaForvalterConsumer.opprettRettighet(rettigheter);
    }
}
