package no.nav.testnav.apps.syntvedtakshistorikkservice.service.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.exception.ArbeidssoekerException;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArenaBrukerUtils {

    private static final Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedInnsatsARBS;
    private static final Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedInnsatsIARBS;
    private static final Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedFormidlingsgruppe;

    private final ServiceUtils serviceUtils;

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

    public static void checkNyeBrukereResponse(NyeBrukereResponse nyeBrukereResponse, String personident) {
        if (nyeBrukereResponse == null) {
            log.error("Kunne ikke opprette ny bruker med fnr {} i Arena: {}", personident, "Ukjent feil.");
            throw new ArbeidssoekerException("Kunne ikke opprette bruker i Arena");
        } else if (nyeBrukereResponse.getNyBrukerFeilList() != null && !nyeBrukereResponse.getNyBrukerFeilList().isEmpty()) {
            log.error("Kunne ikke opprette ny bruker med fnr {} i Arena: {}", personident, nyeBrukereResponse.getNyBrukerFeilList().get(0).getMelding());
            throw new ArbeidssoekerException("Kunne ikke opprette bruker i Arena");
        }
    }

    public Kvalifiseringsgrupper velgKvalifiseringsgruppeBasertPaaFormidlingsgruppe(String aktivitetsfase, String formidlingsgruppe) {
        if (formidlingsgruppe.equals("IARBS")) {
            return velgKvalifiseringsgruppeBasertPaaAktivitetsfase(aktivitetsfase, aktivitestsfaserMedInnsatsIARBS);
        } else {
            return velgKvalifiseringsgruppeBasertPaaAktivitetsfase(aktivitetsfase, aktivitestsfaserMedInnsatsARBS);
        }
    }

    public String velgFormidlingsgruppeBasertPaaAktivitetsfase(String aktivitetsfase) {
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

    public static List<String> hentIdentListe(
            List<Arbeidsoeker> arbeidsoekere
    ) {
        if (arbeidsoekere.isEmpty()) {
            return new ArrayList<>();
        }

        return arbeidsoekere.stream().map(Arbeidsoeker::getPersonident).collect(Collectors.toList());
    }
}
