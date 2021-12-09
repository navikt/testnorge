package no.nav.registre.testnorge.arena.service;

import static java.lang.Math.floor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.BrukereArenaForvalterConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.request.EndreInnsatsbehovRequest;
import no.nav.registre.testnorge.arena.service.exception.VedtakshistorikkException;
import no.nav.registre.testnorge.arena.service.util.ArenaBrukerUtils;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.NyBruker;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.NyEndreInnsatsbehov;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.RettighetType;

import static no.nav.registre.testnorge.arena.service.util.ArenaBrukerUtils.checkNyeBrukereResponse;
import static no.nav.registre.testnorge.arena.service.util.ArenaBrukerUtils.hentIdentListe;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MAKSIMUM_ALDER;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MINIMUM_ALDER;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArenaBrukerService {

    private static final double PROSENTANDEL_SOM_SKAL_HA_MELDEKORT = 0.2;

    private static final String INGEN_OPPFOELGING = "N";
    private static final String MED_OPPFOELGING = "J";
    private static final String IARBS_HOVEDMAAL = "BEHOLDEA";

    private final BrukereArenaForvalterConsumer brukereArenaForvalterConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;
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

        return brukereArenaForvalterConsumer.sendTilArenaForvalter(nyeBrukere);
    }

    public NyeBrukereResponse opprettArbeidsoekere(
            Integer antallNyeIdenter,
            Long avspillergruppeId,
            String miljoe
    ) {
        var levendeIdenter = hentLevendeIdenter(avspillergruppeId);
        var arbeidsoekerIdenter = hentEksisterendeArbeidsoekerIdenter(true);

        if (antallNyeIdenter == null) {
            var antallArbeidsoekereAaOpprette = getAntallBrukereForAaFylleArenaForvalteren(levendeIdenter.size(), arbeidsoekerIdenter.size());

            if (antallArbeidsoekereAaOpprette > 0) {
                antallNyeIdenter = antallArbeidsoekereAaOpprette;
            } else {
                log.info("{}% av gyldige brukere funnet av hodejegeren er allerede registrert i Arena.",
                        (PROSENTANDEL_SOM_SKAL_HA_MELDEKORT * 100));
                return new NyeBrukereResponse();
            }
        }

        var nyeIdenter = arenaBrukerUtils.hentKvalifiserteIdenter(antallNyeIdenter, levendeIdenter, arbeidsoekerIdenter);
        return sendArbeidssoekereTilArenaForvalter(nyeIdenter, miljoe, Kvalifiseringsgrupper.IKVAL, INGEN_OPPFOELGING, null);
    }

    private int getAntallBrukereForAaFylleArenaForvalteren(
            int antallLevendeIdenter,
            int antallEksisterendeIdenter
    ) {
        return (int) (floor(antallLevendeIdenter * PROSENTANDEL_SOM_SKAL_HA_MELDEKORT) - antallEksisterendeIdenter);
    }

    public NyeBrukereResponse opprettArbeidssoeker(
            String ident,
            Long avspillergruppeId,
            String miljoe,
            boolean useCache
    ) {
        var levendeIdenter = hentLevendeIdenter(avspillergruppeId);
        var arbeidsoekerIdenter = hentEksisterendeArbeidsoekerIdenter(useCache);

        if (arbeidsoekerIdenter.contains(ident)) {
            log.info("Ident {} er allerede registrert som arbeidsøker.", ident.replaceAll("[\r\n]", ""));
            var response = new NyeBrukereResponse();
            response.setArbeidsoekerList(brukereArenaForvalterConsumer.hentArbeidsoekere(ident, null, null, useCache));
            return response;
        } else if (!levendeIdenter.contains(ident)) {
            log.info("Ident {} kunne ikke bli funnet av Hodejegeren, og kan derfor ikke opprettes i Arena.", ident.replaceAll("[\r\n]", ""));
            return new NyeBrukereResponse();
        }

        return sendArbeidssoekereTilArenaForvalter(Collections.singletonList(ident), miljoe, Kvalifiseringsgrupper.IKVAL, INGEN_OPPFOELGING, null);
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
        var identerIArena = hentEksisterendeArbeidsoekerIdent(personident, false);
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

    public void opprettArbeidssoekerAap(
            String personident,
            String miljoe,
            String aktivitetsfase,
            LocalDate aktiveringsDato
    ) {
        if (aktivitetsfase == null || aktivitetsfase.isBlank()) {
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

    public List<String> slettBrukereIArenaForvalter(
            List<String> identerToDelete,
            String miljoe
    ) {
        List<String> slettedeIdenter = new ArrayList<>();

        for (var personident : identerToDelete) {
            if (brukereArenaForvalterConsumer.slettBruker(personident, miljoe)) {
                slettedeIdenter.add(personident);
            }
        }

        return slettedeIdenter;
    }

    public List<Arbeidsoeker> hentArbeidsoekere(
            String eier,
            String miljoe,
            String personident,
            boolean useCache
    ) {
        return brukereArenaForvalterConsumer.hentArbeidsoekere(personident, eier, miljoe, useCache);
    }

    public List<String> hentEksisterendeArbeidsoekerIdenter(boolean useCache) {
        var arbeidsoekere = brukereArenaForvalterConsumer.hentArbeidsoekere(null, null, null, useCache);
        return hentIdentListe(arbeidsoekere);
    }

    public List<String> hentEksisterendeArbeidsoekerIdent(String personident, boolean useCache) {
        var arbeidsoekere = brukereArenaForvalterConsumer.hentArbeidsoekere(personident, null, null, useCache);
        return hentIdentListe(arbeidsoekere);
    }

    public List<String> hentEksisterendeArbeidsoekerIdenter(String eier, String miljoe, boolean useCache) {
        var arbeidsoekere = brukereArenaForvalterConsumer.hentArbeidsoekere(null, eier, miljoe, useCache);
        return hentIdentListe(arbeidsoekere);
    }

    private List<String> hentLevendeIdenter(
            Long avspillergruppeId
    ) {
        return hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER);
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
        brukereArenaForvalterConsumer.endreInnsatsbehovForBruker(request);
    }
}
