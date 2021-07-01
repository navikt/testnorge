package no.nav.registre.testnorge.arena.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.service.exception.ArbeidssoekerException;
import no.nav.registre.testnorge.arena.service.exception.VedtakshistorikkException;
import no.nav.registre.testnorge.arena.service.util.KodeMedSannsynlighet;
import no.nav.registre.testnorge.arena.service.util.ServiceUtils;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.RettighetType;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidssoekerService {

    private static final Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedInnsatsARBS;
    private static final Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedInnsatsIARBS;
    private static final Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedFormidlingsgruppe;

    private static final String INGEN_OPPFOELGING = "N";

    private final Random rand;
    private final BrukereService brukereService;
    private final ServiceUtils serviceUtils;
    private final InnsatsService innsatsService;
    private final IdentService identService;

    static {
        aktivitestsfaserMedInnsatsARBS = new HashMap<>();
        aktivitestsfaserMedInnsatsIARBS = new HashMap<>();
        aktivitestsfaserMedFormidlingsgruppe = new HashMap<>();
        URL resourceInnsatserARBS = Resources.getResource("files/ARBS_aktfase_til_innsats.json");
        URL resourceInnsatserIARBS = Resources.getResource("files/IARBS_aktfase_til_innsats.json");
        URL resourceFormidling = Resources.getResource("files/aktfase_til_formidling.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, List<KodeMedSannsynlighet>> mapFormidling = objectMapper.readValue(resourceFormidling, new TypeReference<>() {
            });
            Map<String, List<KodeMedSannsynlighet>> mapARBS = objectMapper.readValue(resourceInnsatserARBS, new TypeReference<>() {
            });
            Map<String, List<KodeMedSannsynlighet>> mapIARBS = objectMapper.readValue(resourceInnsatserIARBS, new TypeReference<>() {
            });

            aktivitestsfaserMedFormidlingsgruppe.putAll(mapFormidling);
            aktivitestsfaserMedInnsatsARBS.putAll(mapARBS);
            aktivitestsfaserMedInnsatsIARBS.putAll(mapIARBS);
        } catch (IOException e) {
            log.error("Kunne ikke laste inn aktivitetsfase fordeling(er).", e);
        }
    }

    public boolean arbeidssoekerIkkeOpprettetIArena(String personident) {
        var identerIArena = identService.hentEksisterendeArbeidsoekerIdent(personident, false);
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
            opprettArbeidssoekerTiltak(personident, miljoe, aktiveringsDato);
        } else if (senesteVedtak.getRettighetType() == RettighetType.TILLEGG) {
            opprettArbeidssoekerTillegg(personident, miljoe, aktiveringsDato);
        } else {
            throw new VedtakshistorikkException("Mangler støtte for rettighettype: " + senesteVedtak.getRettighetType());
        }
    }

    public Kvalifiseringsgrupper opprettArbeidssoekerTiltaksdeltakelse(
            String personident,
            String miljoe,
            NyttVedtak senesteVedtak,
            LocalDate aktiveringsDato
    ) {
        Kvalifiseringsgrupper kvalifiseringsgruppe;
        if (senesteVedtak.getRettighetType() == RettighetType.AAP) {
            kvalifiseringsgruppe = rand.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.VARIG;
        } else if (senesteVedtak.getRettighetType() == RettighetType.TILTAK) {
            kvalifiseringsgruppe = rand.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.BFORM;
        } else if (senesteVedtak.getRettighetType() == RettighetType.TILLEGG) {
            kvalifiseringsgruppe = rand.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.BFORM;
        } else {
            throw new VedtakshistorikkException("Mangler støtte for rettighettype: " + senesteVedtak.getRettighetType());
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
            var formidlingsgruppe = velgFormidlingsgruppeBasertPaaAktivitetsfase(aktivitetsfase);
            var kvalifiseringsgruppe = velgKvalifiseringsgruppeBasertPaaFormidlingsgruppe(aktivitetsfase, formidlingsgruppe);
            opprettArbeidssoeker(personident, miljoe, kvalifiseringsgruppe, aktiveringsDato);

            if (formidlingsgruppe.equals("IARBS")) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.warn("Thread interrupted");
                }
                innsatsService.endreTilFormidlingsgruppeIarbs(personident, miljoe, kvalifiseringsgruppe);
            }
        }
    }

    public void opprettArbeidssoekerTiltak(
            String personident,
            String miljoe,
            LocalDate aktiveringsDato
    ) {
        opprettArbeidssoeker(personident, miljoe, rand.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.BFORM, aktiveringsDato);
    }

    public void opprettArbeidssoekerTillegg(
            String personident,
            String miljoe,
            LocalDate aktiveringsDato
    ) {
        opprettArbeidssoeker(personident, miljoe, rand.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.BFORM, aktiveringsDato);
    }

    private void opprettArbeidssoeker(
            String personident,
            String miljoe,
            Kvalifiseringsgrupper kvalifiseringsgruppe,
            LocalDate aktiveringsDato
    ) {
        if (arbeidssoekerIkkeOpprettetIArena(personident)) {
            var nyeBrukereResponse = brukereService.sendArbeidssoekereTilArenaForvalter(Collections.singletonList(personident), miljoe, kvalifiseringsgruppe, INGEN_OPPFOELGING, aktiveringsDato);
            checkNyeBrukereResponse(nyeBrukereResponse, personident);
        }
    }

    private void checkNyeBrukereResponse(NyeBrukereResponse nyeBrukereResponse, String personident) {
        if (nyeBrukereResponse == null) {
            log.error("Kunne ikke opprette ny bruker med fnr {} i Arena: {}", personident, "Ukjent feil.");
            throw new ArbeidssoekerException("Kunne ikke opprette bruker i Arena");
        } else if (nyeBrukereResponse.getNyBrukerFeilList() != null && !nyeBrukereResponse.getNyBrukerFeilList().isEmpty()) {
            log.error("Kunne ikke opprette ny bruker med fnr {} i Arena: {}", personident, nyeBrukereResponse.getNyBrukerFeilList().get(0).getMelding());
            throw new ArbeidssoekerException("Kunne ikke opprette bruker i Arena");
        }
    }

    private Kvalifiseringsgrupper velgKvalifiseringsgruppeBasertPaaFormidlingsgruppe(String aktivitetsfase, String formidlingsgruppe) {
        if (formidlingsgruppe.equals("IARBS")) {
            return velgKvalifiseringsgruppeBasertPaaAktivitetsfase(aktivitetsfase, aktivitestsfaserMedInnsatsIARBS);
        } else {
            return velgKvalifiseringsgruppeBasertPaaAktivitetsfase(aktivitetsfase, aktivitestsfaserMedInnsatsARBS);
        }
    }

    private String velgFormidlingsgruppeBasertPaaAktivitetsfase(String aktivitetsfase) {
        if (aktivitestsfaserMedFormidlingsgruppe.containsKey(aktivitetsfase)) {
            return serviceUtils.velgKodeBasertPaaSannsynlighet(aktivitestsfaserMedFormidlingsgruppe.get(aktivitetsfase)).getKode();
        } else {
            throw new ArbeidssoekerException("Ukjent aktivitetsfase " + aktivitetsfase);
        }
    }

    private Kvalifiseringsgrupper velgKvalifiseringsgruppeBasertPaaAktivitetsfase(
            String aktivitetsfase,
            Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedInnsats) {
        if (aktivitestsfaserMedInnsats.containsKey(aktivitetsfase)) {
            var innsats = serviceUtils.velgKodeBasertPaaSannsynlighet(aktivitestsfaserMedInnsats.get(aktivitetsfase)).getKode();
            return Kvalifiseringsgrupper.valueOf(innsats);
        } else {
            throw new ArbeidssoekerException("Ukjent aktivitetsfase " + aktivitetsfase);
        }
    }
}
