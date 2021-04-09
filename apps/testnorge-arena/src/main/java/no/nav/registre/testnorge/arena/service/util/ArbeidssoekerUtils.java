package no.nav.registre.testnorge.arena.service.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetRequest;
import no.nav.registre.testnorge.arena.service.BrukereService;
import no.nav.registre.testnorge.arena.service.InnsatsService;
import no.nav.registre.testnorge.arena.service.exception.ArbeidssoekerException;
import no.nav.registre.testnorge.arena.service.exception.VedtakshistorikkException;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.RettighetType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidssoekerUtils {

    private static final Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedInnsatsARBS;
    private static final Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedInnsatsIARBS;
    private static final Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedFormidlingsgruppe;

    private static final String INGEN_OPPFOELGING = "N";

    private final Random rand;
    private final BrukereService brukereService;
    private final ServiceUtils serviceUtils;
    private final InnsatsService innsatsService;
    private final IdenterUtils identerUtils;

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


    public boolean arbeidssoekerIkkeOpprettetIArena(String personident){
        var identerIArena = identerUtils.hentEksisterendeArbeidsoekerIdent(personident,false);
        return !identerIArena.contains(personident);
    }


    public void opprettArbeidssoekerVedtakshistorikk(
            String personident,
            String miljoe,
            NyttVedtak senesteVedtak
    ) {
        if (senesteVedtak.getRettighetType() == RettighetType.AAP) {
            opprettArbeidssoekerAap(personident, miljoe, ((NyttVedtakAap) senesteVedtak).getAktivitetsfase());
        } else if (senesteVedtak.getRettighetType() == RettighetType.TILTAK) {
            opprettArbeidssoekerTiltak(personident, miljoe);
        } else if (senesteVedtak.getRettighetType() == RettighetType.TILLEGG) {
            opprettArbeidssoekerTillegg(personident, miljoe);
        } else {
            throw new VedtakshistorikkException("Mangler støtte for rettighettype: " + senesteVedtak.getRettighetType());
        }
    }

    public Kvalifiseringsgrupper opprettArbeidssoekerTiltaksdeltakelse(
            String personident,
            String miljoe,
            NyttVedtak senesteVedtak
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

        opprettArbeidssoeker(personident, miljoe, kvalifiseringsgruppe);
        return kvalifiseringsgruppe;
    }

    public void opprettArbeidssoekerAap(
            String personident,
            String miljoe
    ) {
        opprettArbeidssoeker(personident, miljoe, rand.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.VARIG);
    }

    public void opprettArbeidssoekerAap(
            String personident,
            String miljoe,
            String aktivitetsfase
    ) {
        if (aktivitetsfase == null || aktivitetsfase.isBlank()) {
            opprettArbeidssoeker(personident, miljoe, Kvalifiseringsgrupper.BATT);
        } else {
            var formidlingsgruppe = velgFormidlingsgruppeBasertPaaAktivitetsfase(aktivitetsfase);
            var kvalifiseringsgruppe = velgKvalifiseringsgruppeBasertPaaFormidlingsgruppe(aktivitetsfase, formidlingsgruppe);
            opprettArbeidssoeker(personident, miljoe, kvalifiseringsgruppe);

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
            String miljoe
    ) {
        opprettArbeidssoeker(personident, miljoe, rand.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.BFORM);
    }

    public void opprettArbeidssoekerTillegg(
            String personident,
            String miljoe
    ) {
        opprettArbeidssoeker(personident, miljoe, rand.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.BFORM);
    }


    public void opprettArbeidssoeker(
            String personident,
            String miljoe,
            Kvalifiseringsgrupper kvalifiseringsgruppe
    ) {
        if (arbeidssoekerIkkeOpprettetIArena(personident)) {
            var nyeBrukereResponse = brukereService.sendArbeidssoekereTilArenaForvalter(Collections.singletonList(personident), miljoe, kvalifiseringsgruppe, INGEN_OPPFOELGING);
            if (nyeBrukereResponse != null && nyeBrukereResponse.getNyBrukerFeilList() != null && !nyeBrukereResponse.getNyBrukerFeilList().isEmpty()) {
                nyeBrukereResponse.getNyBrukerFeilList().forEach(nyBrukerFeil -> {
                    log.error("Kunne ikke opprette ny bruker med fnr {} i arena: {}", nyBrukerFeil.getPersonident(), nyBrukerFeil.getMelding());
                    throw new ArbeidssoekerException("Kunne ikke opprette bruker i arena");
                });
            }
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
